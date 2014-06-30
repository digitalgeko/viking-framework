package nl.viking.utils

import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil
import freemarker.template.Configuration
import freemarker.template.Template
import nl.viking.Conf
import nl.viking.VikingPortlet
import nl.viking.controllers.DataHelper
import nl.viking.controllers.router.JSRouterFreemarkerMethod
import nl.viking.controllers.router.JSi18nFreemarkerMethod
import nl.viking.controllers.router.RouterFreemarkerMethod

import javax.servlet.ServletContext

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/20/13
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
class TemplateUtils {

	private static Configuration freemarkerConfigurationSingleton

	static writeToRequest (request, response, outputStream, viewTemplate, data) {
		def cfg = getFreemarkerConfiguration(request, response);
		Template template = cfg.getTemplate(viewTemplate);
		Writer out = new OutputStreamWriter(outputStream);
		fillTemplateVariables(request, response, data)
		template.process(data, out);
		out.flush();
	}

	static fillTemplateVariables(request, response, data) {
		DataHelper dataHelper = new DataHelper(request, response, request)
		data["JS_ROUTER_PARAMETER_PREFIX"] = Conf.JS_ROUTER_PARAMETER_PREFIX
		data["route"] = new RouterFreemarkerMethod(response:response, request: request)
		data["jsRoute"] = new JSRouterFreemarkerMethod(response:response, request: request)
		data["jsi18n"] = new JSi18nFreemarkerMethod(response:response, request: request, h: dataHelper)
		data["h"] = dataHelper
		data["request"] = dataHelper.servletRequest
	}

	static Configuration getFreemarkerConfiguration(request, response) {
		println "new freemarker configuration"
		if (freemarkerConfigurationSingleton == null) {
			ServletContext servletContext = VikingPortlet.currentServletContext;
			freemarkerConfigurationSingleton = new Configuration();
			if (Conf.properties.dev.enabled) {
				freemarkerConfigurationSingleton.setDirectoryForTemplateLoading(new File(Conf.properties.dev.views))
			} else {
				freemarkerConfigurationSingleton.setServletContextForTemplateLoading(servletContext, "/WEB-INF/views")
			}
			freemarkerConfigurationSingleton.addAutoInclude("viking_macros/errors.ftl")
			freemarkerConfigurationSingleton.addAutoInclude("viking_macros/notifications.ftl")

		}
		freemarkerConfigurationSingleton
	}
}
