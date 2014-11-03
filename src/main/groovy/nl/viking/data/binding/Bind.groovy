package nl.viking.data.binding

import com.liferay.portal.kernel.upload.UploadPortletRequest
import com.liferay.portal.util.PortalUtil
import groovy.json.JsonSlurper
import nl.viking.Conf
import nl.viking.data.validation.Validator
import nl.viking.logging.Logger
import nl.viking.model.morphia.Blob
import org.codehaus.jackson.annotate.JsonAutoDetect
import org.codehaus.jackson.annotate.JsonMethod
import org.codehaus.jackson.map.DeserializationConfig
import org.codehaus.jackson.map.ObjectMapper

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/2/13
 * Time: 8:57 PM
 * To change this template use File | Settings | File Templates.
 */
class Bind {

	Validator validator

	def request

	static <T> T fromRequest(String paramName, request, Class<T> clazz, Class listClass = null, T targetObject = null) {
		new Bind(request: request).bind(paramName, clazz, listClass, targetObject)
	}

	def getUploadRequest() {
		if (Conf.properties.allowParametersWithoutPrefix && request.method.equalsIgnoreCase("POST")) {
			return request
		}

		if (!PortalUtil.isMultipartRequest(PortalUtil.getHttpServletRequest(request))) {
			throw new Exception("Form request is not multipart/form-data, add [enctype='multipart/form-data'] attribute to your HTML form")
		}
		return PortalUtil.getUploadPortletRequest(request)
	}

	def <T> T bind(String paramName, Class<T> clazz, Class listClass = null, T targetObject = null) {
        if (File.class.isAssignableFrom(clazz)) {
			def uploadRequest = getUploadRequest()
            return uploadRequest.getFile(paramName)
        } else if (Blob.class.isAssignableFrom(clazz)){
			def uploadRequest = getUploadRequest()
			return Blob.create(uploadRequest.getFile(paramName), uploadRequest.getFileName(paramName))
        } else {
            def values = request.getParameterValues(paramName)
            if (values && values.size() > 0) {
				if (Collection.class.isAssignableFrom(clazz)) {
					return values.collect {
						valueFromRequest(it, paramName, request, listClass)
					}
                } else {
					return valueFromRequest(values[0], paramName, request, clazz)
                }
            } else {
                return pojoFromRequest(paramName, request, clazz, targetObject)
            }
        }
        return null
    }

    def firstElement(String str, String parentName) {
        str = str - (parentName+".")
        if (str.indexOf(".") >= 0) {
            return str.substring(0, str.indexOf("."))
        }
        return str
    }

	def <T> T valueFromRequest(String value, String paramName, request, Class<T> clazz) {

		if (Number.class.isAssignableFrom(clazz) && !value.isNumber()) {
			if (validator) {
				validator.addError(paramName, "validator.invalid.number")
			}
			return null
		} else if (Long.class.isAssignableFrom(clazz)) {
			return value ? new Long(value) :null
        } else if (Integer.class.isAssignableFrom(clazz)) {
            return value ? new Integer(value) :null
		} else if (String.class.isAssignableFrom(clazz)) {
			return value ? value.toString() :null

		} else if (Boolean.class.isAssignableFrom(clazz)) {
            if (value) return value != "false"
			else return null

        } else if (value != null && value.isEmpty() && !String.class.isAssignableFrom(clazz)) {
			return null

		} else {
            return value.asType(clazz)
        }
    }

    def <T> T pojoFromRequest(String paramName, request, Class<T> clazz, T targetObject = null) {
        def objParameterNames = request.getParameterNames().toList().findAll{
            it.startsWith(paramName+".")
        }.unique{ a, b ->
            firstElement(a, paramName) <=> firstElement(b, paramName)
        }.collect{
            paramName + "." + firstElement(it, paramName)
        }

        if (!objParameterNames.isEmpty()) {
            if (clazz == Date.class) {
                return dateFromRequest(paramName, request)
            }

			if (!targetObject) {
				targetObject = clazz.newInstance()
			}

            def isModel = nl.viking.model.morphia.Model.class.isAssignableFrom(clazz) || nl.viking.model.hibernate.Model.class.isAssignableFrom(clazz)
            if (isModel){
                def idField = paramName+".id"
                if (objParameterNames.contains(idField) && request.getParameter(idField)) {
                    targetObject = clazz.findById(request.getParameter(idField))
                }
            }

            objParameterNames.each {
                String propName = it.replace(paramName+".", "")
				if (propName != "id") {
					try {
						def fieldDef = clazz.getDeclaredField(propName)
						Class listClass = null
						if (List.class.isAssignableFrom(fieldDef.type)) {
							listClass = fieldDef.genericType.actualTypeArguments[0]
						}
						targetObject[propName] = bind(it, fieldDef.type, listClass)
					} catch (java.lang.NoSuchFieldException e) {
						def field = clazz.declaredFields.find { it.name == propName }
						if (field || Map.class.isAssignableFrom(clazz)){
							targetObject[propName] = request.getParameter(it).asType(field?.type ?: String.class)
						}
					}
				}
            }
            return targetObject
        }
        return null
    }

    def Date dateFromRequest(String paramName, request) {
        Calendar cal = Calendar.getInstance()
        Integer date = new Integer(request.getParameter(paramName+".date"))
        Integer month = new Integer(request.getParameter(paramName+".month"))
        Integer year = new Integer(request.getParameter(paramName+".year"))

        if (date) cal.set(Calendar.DATE, date)
        if (month) cal.set(Calendar.MONTH, month)
        if (year) cal.set(Calendar.YEAR, year)

        return cal.getTime()
    }

	static isModel(Class clazz) {
		nl.viking.model.morphia.Model.class.isAssignableFrom(clazz) || nl.viking.model.hibernate.Model.class.isAssignableFrom(clazz)
	}

	def <T> T fromJsonBody(Class<T> clazz = Object.class) {
		InputStream inputStream
		if (request.hasProperty("inputStream")) {
			inputStream = request.inputStream
		} else {
			inputStream = request.portletInputStream
		}

		def jsonText = inputStream.text
		fromJson(jsonText, clazz)
	}

	static fromJson(String jsonText, Class clazz, boolean retrieveModelById = true) {
		ObjectMapper mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		def tempObj = mapper.readValue(jsonText, clazz)

		def targetObject
		Map json = new JsonSlurper().parseText(jsonText)
		if (retrieveModelById && isModel(clazz) && json.id) {
			targetObject = clazz.findById(json.id)
			targetObject.properties.findAll {!['id', "_id", "created", "updated", "class"].contains(it.key)}.each {
				if (json.containsKey(it.key)) {
					try {
						targetObject[it.key] = tempObj[it.key]
					} catch (ReadOnlyPropertyException e) {
						Logger.warn("Could not bind property $it.key because is read-only.")
					}
				}
			}
		} else {
			targetObject = tempObj
		}
		targetObject
	}
}
