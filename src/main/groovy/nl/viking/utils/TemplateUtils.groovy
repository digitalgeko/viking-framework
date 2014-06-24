package nl.viking.utils

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
	static writeToRequest (request, response, outputStream, viewTemplate, data) {
		def cfg = getFreemarkerConfiguration(request, response);
		cfg.addAutoInclude("viking_macros/errors.ftl")
		cfg.addAutoInclude("viking_macros/notifications.ftl")

		Template template = cfg.getTemplate(viewTemplate);

		Writer out = new OutputStreamWriter(outputStream);
		template.process(data, out);
		out.flush();
	}

	static Configuration getFreemarkerConfiguration(request, response) {
		ServletContext servletContext = VikingPortlet.currentServletContext;


		DataHelper dataHelper = new DataHelper(request, response, request)
		Configuration cfg = new Configuration();
		cfg.setSharedVariable("JS_ROUTER_PARAMETER_PREFIX", Conf.JS_ROUTER_PARAMETER_PREFIX)
		cfg.setSharedVariable("route", new RouterFreemarkerMethod(response:response, request: request))
		cfg.setSharedVariable("jsRoute", new JSRouterFreemarkerMethod(response:response, request: request))
		cfg.setSharedVariable("jsi18n", new JSi18nFreemarkerMethod(response:response, request: request, h: dataHelper))
		cfg.setSharedVariable("h", dataHelper)
		cfg.setSharedVariable("request", dataHelper.servletRequest)

		if (Conf.properties.dev.enabled) {
			cfg.setDirectoryForTemplateLoading(new File(Conf.properties.dev.views))
		} else {
			cfg.setServletContextForTemplateLoading(servletContext, "/WEB-INF/views");
		}

		cfg
	}
}
