package nl.viking.utils

import com.liferay.portal.kernel.language.Language
import com.liferay.portal.kernel.language.LanguageUtil
import com.liferay.portal.security.permission.ResourceActionsUtil
import groovy.text.SimpleTemplateEngine
import nl.viking.model.annotation.ModelResource
import org.reflections.Reflections

import javax.portlet.PortletContext

/**
 * User: mardo
 * Date: 10/15/14
 * Time: 10:05 AM
 */
class ModelResourcesUtils {

	static registerAllModels(PortletContext portletContext) {
		def reflections = new Reflections("models")
		def modelResourceTemplate = new SimpleTemplateEngine().createTemplate(ModelResourcesUtils.classLoader.getResource("templates/model-resource-template.xml"))

		reflections.getTypesAnnotatedWith(ModelResource.class).each { modelClass ->
			ModelResource modelResourceAnnotation = modelClass.annotations.find {it instanceof ModelResource}

			def data = [
			        modelName: modelClass.name,
					portlets: modelResourceAnnotation.portlets(),
					weight: modelResourceAnnotation.weight(),
					supports: modelResourceAnnotation.supports(),
					siteMemberDefaults: modelResourceAnnotation.siteMemberDefaults(),
					guestDefaults: modelResourceAnnotation.guestDefaults(),
					guestUnsupported: modelResourceAnnotation.guestUnsupported(),
			]

			if (!data.portlets) {
				def liferayPortletXML = new XmlSlurper().parse(portletContext.getResourceAsStream("/WEB-INF/liferay-portlet.xml"))
				data.portlets = liferayPortletXML.portlet."portlet-name".collect { it }
			}

			def modelResourceXMLString = modelResourceTemplate.make(data).toString()
			InputStream modelResourceXMLInputStream = new ByteArrayInputStream(modelResourceXMLString.bytes)
			ResourceActionsUtil.read(portletContext.portletContextName, modelResourceXMLInputStream)
		}
	}

}
