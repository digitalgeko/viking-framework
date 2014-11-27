package nl.viking.utils

import com.liferay.portal.kernel.language.LanguageUtil
import freemarker.template.Configuration
import freemarker.template.Template
import groovy.text.SimpleTemplateEngine
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

	static writeToOutputStream (viewTemplate, outputStream, data) {
		def cfg = getFreemarkerConfiguration();
		Template template = cfg.getTemplate(viewTemplate);
		Writer out = new OutputStreamWriter(outputStream);
		template.process(data, out);
		out.flush();
	}
	static writeToRequest (request, response, outputStream, viewTemplate, data) {
		fillTemplateVariables(request, response, data)
		writeToOutputStream(viewTemplate, outputStream, data)
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

	static Configuration getFreemarkerConfiguration() {
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
			if (freemarkerConfigurationSingleton.getTemplate("viking_macros/header.ftl")) {
				freemarkerConfigurationSingleton.addAutoInclude("viking_macros/header.ftl")
			}

		}
		freemarkerConfigurationSingleton
	}

	static String i18nTemplate(Locale locale, String i18nKey, data) {
		def engine = new SimpleTemplateEngine()
		def titleTemplate = LanguageUtil.get(locale, i18nKey)
		if (titleTemplate) {
			return engine.createTemplate(titleTemplate).make(data).toString()
		}
		return null
	}
}
