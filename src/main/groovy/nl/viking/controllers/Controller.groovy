package nl.viking.controllers

import com.liferay.portal.kernel.dao.orm.DynamicQuery
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil
import com.liferay.portal.kernel.servlet.HttpHeaders
import com.liferay.portal.kernel.util.MimeTypesUtil
import com.liferay.portal.kernel.util.PortalClassLoaderUtil
import com.liferay.portlet.asset.model.AssetTag
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil
import com.mongodb.gridfs.GridFSDBFile
import nl.viking.Conf
import nl.viking.DateSerializer
import nl.viking.VikingPortlet
import nl.viking.controllers.annotation.Resource
import nl.viking.controllers.response.DoNothing
import nl.viking.controllers.response.Redirect
import nl.viking.controllers.router.Router
import nl.viking.data.binding.Bind
import nl.viking.data.validation.Validator
import nl.viking.utils.TemplateUtils
import org.apache.commons.io.IOUtils
import org.codehaus.jackson.Version
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.SerializationConfig
import org.codehaus.jackson.map.module.SimpleModule

import javax.portlet.*

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/3/13
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class Controller {

    def request

    def response

	VikingPortlet portlet

	def portletRequest

    OutputStream outputStream

    def contentType

    def viewTemplate

    Validator validator

    DataHelper h

	PortletPreferences preferences

    ObjectMapper json

	Bind binder

	def init() {
		this.validator = new Validator(getH())
		this.binder = new Bind(validator: this.validator, request: this.request)
		this.json = new ObjectMapper()
        this.json.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        final SimpleModule jsonModule = new SimpleModule("viking", Version.unknownVersion());
        jsonModule.addSerializer(Date.class, new DateSerializer())
        this.json.registerModule(jsonModule)
    }

	void setResponse(def response) {
        this.response = response
        if (!(response instanceof ActionResponse)) {
            outputStream = response.getPortletOutputStream()
        }
    }

    DataHelper getH() {
        if (h == null) {
			h = new DataHelper(request, response, portletRequest, this.class)
        }
        return h
    }

	static DataHelper getCurrentDataHelper () {
		return VikingPortlet.currentController?.h
	}

    PortletPreferences getPreferences() {
        if (preferences == null) {
			if (portletRequest) {
				preferences = portletRequest.preferences
			}else{
				preferences = request.preferences
			}
        }
        return preferences
    }

    String route(route, LinkedHashMap params = []) {
        new Router(response: response, request: request).route(route, params)
    }

    def redirect(String routeOrUrl, LinkedHashMap params = []) {
        if (routeOrUrl.contains("/")) {
            return new Redirect(url: routeOrUrl)
        }
        return new Redirect(action: routeOrUrl, url: route(routeOrUrl, params))
    }

    def render(LinkedHashMap data = []) {
        render(null, data)
    }

    def renderBinary(GridFSDBFile file, String fileName, Boolean download = true) {
        if (download) {
            String contentDisposition = "attachment; filename=$fileName";
            response.setProperty(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        }
        renderBinary(file)
    }

    def renderBinary(GridFSDBFile file) {
        response.contentType = this.contentType?:MimeTypesUtil.getContentType(file.filename);
        renderBinary(file.inputStream)
    }

	def renderBinary(File file, String fileName = file.name) {
		FileInputStream fileInputStream = new FileInputStream(file)
		renderBinary(fileInputStream, fileName)
	}

    def renderBinary(InputStream is, String fileName, Boolean download = true) {
        if (download) {
            String contentDisposition = "attachment; filename=$fileName"
            response.setProperty(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        }
        response.contentType = this.contentType?:MimeTypesUtil.getContentType(fileName)
        renderBinary(is)
    }

    def render(String viewTemplate, LinkedHashMap data = []) {
        if (!viewTemplate) viewTemplate = this.viewTemplate

		if (!data.angularData) {
			data.angularData = [:]
		}
		data.angularData.VIKING_FRAMEWORK_PARAMS = [
				controllerName: this.class.name
		]
		data.angularData = toJson(data.angularData)

        if (validator.errors.size() > 0) {
            data << [vikingErrors: validator.errors]
        }

        if (!(response instanceof ActionResponse)) {
            TemplateUtils.writeToRequest(portletRequest, response, outputStream, viewTemplate, data)
        } else {
			portletRequest.setAttribute(Conf.REQUEST_TEMPLATE_KEY+portlet.portletName, viewTemplate)
			portletRequest.setAttribute(Conf.REQUEST_DATA_KEY+portlet.portletName, data)
        }

        new DoNothing()
    }

    def renderBinary(InputStream is) {
		try {
			IOUtils.copy(is, outputStream)
		} finally {
			IOUtils.closeQuietly(is)
		}
        new DoNothing()
    }

    def renderString(String str) {
        if (!(response instanceof ActionResponse)) {
            outputStream.write(str.bytes)
        } else {
            request.setAttribute(Conf.REQUEST_STRING_DATA_KEY+portlet.portletName, str)
        }
        new DoNothing()
    }

    // Json
    def renderJSON(data) {
        response.contentType = this.contentType?:"application/json"
        this.json.writeValue(outputStream, data)
        new DoNothing()
    }

    String toJson(data) {
        this.json.writeValueAsString(data)
    }

    // Binders
    def <T> T bind(String parameterName, Class<T> clazz, T defaultValue = null, T targetObject = null){
        def obj = binder.bind(parameterName, clazz, null, targetObject)
        if (obj == null) {
            return defaultValue
        }
        return obj
    }

    def Long bindLong (String parameterName, Long defaultValue = null) {
        bind(parameterName, Long.class, defaultValue)
    }
    def String bindString (String parameterName, String defaultValue = null) {
        bind(parameterName, String.class, defaultValue)
    }
    def Boolean bindBoolean (String parameterName, Boolean defaultValue = null) {
        bind(parameterName, Boolean.class, defaultValue)
    }
    def Integer bindInteger (String parameterName, Integer defaultValue = null) {
        bind(parameterName, Integer.class, defaultValue)
    }
    def File bindFile (String parameterName, File defaultValue = null) {
        bind(parameterName, File.class, defaultValue)
    }

	def List bindFiles (String parameterName, List defaultValue = null) {
		def obj = binder.bind(parameterName, File.class, List.class)
		if (obj == null) {
			return defaultValue
		}
		return obj
	}

	def <T> T bindJsonBody (Class<T> clazz = Object.class) {
		binder.fromJsonBody(clazz)
	}


    RenderRequest getRenderRequest() {
        if (request instanceof RenderRequest){
            return (RenderRequest) request
        }
        return null
    }

    ActionRequest getActionRequest() {
        if (request instanceof ActionRequest){
            return (ActionRequest) request
        }
        return null
    }

    ResourceRequest getResourceRequest() {
        if (request instanceof ResourceRequest){
            return (ResourceRequest) request
        }
        return null
    }

	RenderResponse getRenderResponse() {
		if (response instanceof RenderResponse){
			return (RenderResponse) response
		}
		return null
	}

	ActionResponse getActionResponse() {
		if (response instanceof ActionResponse){
			return (ActionResponse) response
		}
		return null
	}

	ResourceResponse getResourceResponse() {
		if (response instanceof ResourceResponse){
			return (ResourceResponse) response
		}
		return null
	}

	def getTemplatesFolder() {
		return this.class.simpleName
	}


	// Default methods

	@Resource(mode="view")
	def getTags() {
		def params = bindJsonBody()
		final DynamicQuery tagsQuery = DynamicQueryFactoryUtil.forClass(AssetTag.class, PortalClassLoaderUtil.classLoader)
		tagsQuery.add(PropertyFactoryUtil.forName("name").like(params.query+"%"))
        tagsQuery.add(PropertyFactoryUtil.forName("companyId").eq(h.themeDisplay.companyId))
        tagsQuery.add(PropertyFactoryUtil.forName("groupId").eq(h.themeDisplay.scopeGroupId))

		def tags = AssetTagLocalServiceUtil.dynamicQuery(tagsQuery).collect {AssetTag tag -> [text: tag.name, tagId: tag.tagId]}

		renderJSON(tags)
	}
}
