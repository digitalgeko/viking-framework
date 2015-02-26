package nl.viking.controllers.router

import com.liferay.portal.kernel.util.WebKeys
import nl.viking.Conf
import nl.viking.controllers.annotation.Action
import nl.viking.controllers.annotation.Render
import nl.viking.controllers.annotation.Resource

import javax.portlet.PortletMode

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/11/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
class Router {

    def request

    def response

    static String portletName(def request) {
        String portletName = (String)request.getAttribute(WebKeys.PORTLET_ID);
        return portletName.substring(0, portletName.indexOf("_"))
    }

    static controllerPackage(def request) {
        return "controllers"
    }

    public String route(String route, LinkedHashMap params = []) {
        def routeArray = route.split("\\.")
        def controller = Class.forName("${controllerPackage(request)}.${routeArray[0]}")
        def action = routeArray[1]
        def method = controller.getMethod(action)

		PortletMode portletMode
		if (action == "view") {
			portletMode = PortletMode.VIEW
		}else if (action == "edit") {
			portletMode = PortletMode.EDIT
		}

        def portletUrl

		method.annotations.each {
			if (it instanceof Action || it instanceof Render || it instanceof Resource) {
				if (it instanceof Action) {
					portletUrl = response.createActionURL()
				} else if (it instanceof Render) {
					portletUrl = response.createRenderURL()
				} else if (it instanceof Resource) {
					portletUrl = response.createResourceURL()
				}

				if (!portletMode) portletMode = new PortletMode (it.mode().toLowerCase())
				portletUrl.portletMode = portletMode ?: PortletMode.EDIT
				return;
			}
		}

		if (!portletUrl) {
			throw new Exception ("Method $route is not annotated with @Action, @Render or @Resource annotations.")
		}

        params.each {
            if (it.value) {
                portletUrl.setParameter(it.key, it.value.toString())
            }
        }

        portletUrl.setParameter(Conf.REQUEST_CONTROLLER_KEY, controller.getCanonicalName())
        portletUrl.setParameter(Conf.REQUEST_ACTION_KEY, action)

        return portletUrl.toString();
    }

}
