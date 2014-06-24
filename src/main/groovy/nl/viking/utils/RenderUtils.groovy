package nl.viking.utils

import nl.viking.controllers.Controller
import nl.viking.controllers.response.DoNothing
import nl.viking.controllers.response.Redirect
import nl.viking.model.morphia.Blob

import javax.portlet.ActionResponse

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/23/13
 * Time: 9:27 AM
 * To change this template use File | Settings | File Templates.
 */
class RenderUtils {

	static def handleRenderResponse(data, Controller controllerInstance, response) {
		if (!(data instanceof DoNothing)) {
			if (data instanceof String || data instanceof GString) {
				controllerInstance.renderString(data.toString())
			} else if (data instanceof LinkedHashMap || (data instanceof List && data.size() == 0)) {
				controllerInstance.render((LinkedHashMap) data)
			} else if (data instanceof Blob) {
				Blob blob = data
				controllerInstance.renderBinary(data.get())
			} else if (data instanceof File) {
				File file = data
				controllerInstance.renderBinary(file)

			} else if (data == null) {
				controllerInstance.render((LinkedHashMap) [])
			} else if (data instanceof Redirect){
				Redirect redirect = data
				if (response instanceof ActionResponse){
					response.sendRedirect(redirect.url)
				} else {
					throw new Exception("Incorrect use of redirect on response (${response.class}), should only use it with ActionResponse.")
				}
			} else {
				try {
					controllerInstance.render()
				} catch (java.io.FileNotFoundException e){
					controllerInstance.render("")
				}
			}
		}
    }
}
