package nl.viking

import com.liferay.portal.util.PortalUtil
import nl.viking.controllers.Controller
import nl.viking.controllers.annotation.Action
import nl.viking.controllers.annotation.Render
import nl.viking.controllers.annotation.Resource
import nl.viking.db.HibernateFactory
import nl.viking.enhancers.ModelEnhancer
import nl.viking.logging.Logger
import nl.viking.utils.RenderUtils
import nl.viking.utils.TemplateUtils
import org.reflections.Reflections

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.portlet.*
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest

class VikingPortlet extends GenericPortlet
{
	private String defaultControllerName

	public static final ThreadLocal<Controller> controllerThreadLocal = new ThreadLocal();

	public static final ThreadLocal<ServletContext> servletContextThreadLocal = new ThreadLocal();

	public boolean isDevEnabled = false

	static Controller getCurrentController() {
		controllerThreadLocal.get()
	}

	static ServletContext getCurrentServletContext() {
		servletContextThreadLocal.get()
	}

	@PostConstruct
	def void init() {
		Logger.info("Initializing %s...", getPortletName())
		if (!defaultControllerName) {
			def reflections = new Reflections("controllers")
			Set<Class<? extends Controller>> controllers = reflections.getSubTypesOf(Controller.class);
			defaultControllerName = controllers.find{
				if (it) {
					return it.simpleName.toLowerCase() == getPortletName()
				}
				return false
			}.name
		}
		ModelEnhancer.enhanceAllModels()
		isDevEnabled = Conf.properties.dev.enabled
		if (isDevEnabled) {
			Logger.info("Running viking on DEV mode!")
		}
	}

	def loadDevController(controllerName) {
		def cl = new GroovyClassLoader(this.class.classLoader) {
			@Override
			protected void setClassCacheEntry(Class cls) {
				super.setClassCacheEntry(cls)
				this.loadClass("nl.viking.enhancers.ModelEnhancer").enhanceModel(cls)
			}
		}
		List<String> sources = Conf.properties.dev.sources

		cl.setResourceLoader(new GroovyResourceLoader() {
			@Override
			URL loadGroovySource(String filename) throws MalformedURLException {
				if (filename.contains("SoapDataSource")) println "getClassCacheEntry $filename"
				for (it in sources) {
					def file = new File(it, filename.replace('.', '/') + ".groovy")
					if (file.exists()) return file.toURI().toURL()
				}
				return null
			}
		})
		cl.parseClass(new File(cl.resourceLoader.loadGroovySource(controllerName).toURI()))
	}

	def routeMethod (request, response, portletRequest = null) {
		if (portletRequest == null) {
			portletRequest = request
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(portletRequest);
			servletContextThreadLocal.set(servletRequest.session.servletContext)

			if (Conf.properties.allowParametersWithoutPrefix && request.method.equalsIgnoreCase("POST")) {
				request = PortalUtil.getUploadPortletRequest(request)
			}
		} else {
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(portletRequest);
			servletContextThreadLocal.set(servletRequest.session.servletContext)
		}
		PortletMode mode = portletRequest.portletMode

		String controllerName = request.getParameter(Conf.REQUEST_CONTROLLER_KEY)
		if (!controllerName && portletRequest) controllerName = portletRequest.getParameter(Conf.REQUEST_CONTROLLER_KEY)

		def action = request.getParameter(Conf.REQUEST_ACTION_KEY)
		if (!action && portletRequest) action = portletRequest.getParameter(Conf.REQUEST_ACTION_KEY)

		def viewTemplate = null
		if (!controllerName){
			controllerName = defaultControllerName
			if (mode == PortletMode.EDIT){
				action = "edit"
			}else{
				action = "view"
			}
		}


		def controller
		if (isDevEnabled) {
			controller = loadDevController(controllerName)
		} else {
			controller = (Class<Controller>) this.class.classLoader.loadClass(controllerName)
		}

		def controllerInstance = controller.newInstance();
		controllerInstance.request = request
		controllerInstance.response = response
		controllerInstance.portlet = this
		controllerInstance.portletRequest = portletRequest
		controllerInstance.init()
		controllerThreadLocal.set(controllerInstance)

		if (!viewTemplate) viewTemplate = "${controllerInstance.templatesFolder}/${action}.ftl"
		controllerInstance.viewTemplate = viewTemplate

		controller.getMethod(action).annotations.each {
			if (it instanceof Action || it instanceof Render || it instanceof Resource) {
				if (!it.mode() && !['edit', 'view'].contains(action)) {
					def annotationLabel = ""
					if (it instanceof Action ) annotationLabel = "@Action"
					else if (it instanceof Render ) annotationLabel = "@Render"
					else if (it instanceof Resource ) annotationLabel = "@Resource"
					throw new IllegalArgumentException("Missing mode in annotation for method $action, please use $annotationLabel(mode='view') or $annotationLabel(mode='edit')")
				} else {
					if ((action == "edit" && mode == PortletMode.EDIT) || new PortletMode(it.mode().toLowerCase()) == mode || action == "view") {
						def data = controllerInstance.invokeMethod(action, null)
						RenderUtils.handleRenderResponse(data, controllerInstance, response)
					} else {
						throw new SecurityException("Logged in user doesn't have permissions to access ${controllerName}.${action} method")
					}
				}
				return;
			}
		}

		HibernateFactory.closeCurrentSession()
		controllerThreadLocal.remove()
		servletContextThreadLocal.remove()
	}

	@Override
	def void processAction(ActionRequest request, ActionResponse response) {
		if (PortalUtil.isMultipartRequest(PortalUtil.getHttpServletRequest(request))) {
			routeMethod(PortalUtil.getUploadPortletRequest(request), response, request)
		} else {
			routeMethod(request, response)
		}
	}

	@Override
	def void doView(RenderRequest request, RenderResponse response) {
		if (!tryRender(request, response)){
			routeMethod(request, response)
		}
	}

	@Override
	def void doEdit(RenderRequest request, RenderResponse response) {
		if (!tryRender(request, response)){
			routeMethod(request, response)
		}
	}

	def boolean tryRender(RenderRequest request, RenderResponse response) {
		def viewTemplate = request.getAttribute(Conf.REQUEST_TEMPLATE_KEY+portletName)
		String stringData = request.getAttribute(Conf.REQUEST_STRING_DATA_KEY+portletName)

		if (viewTemplate) {
			def data = request.getAttribute(Conf.REQUEST_DATA_KEY+portletName)
			TemplateUtils.writeToRequest(request, response, response.getPortletOutputStream(), viewTemplate, data)
			return true
		}else if (stringData) {
			response.getPortletOutputStream().write(stringData.bytes)
			return true
		}
		return false
	}

	@Override
	void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		if (PortalUtil.isMultipartRequest(PortalUtil.getHttpServletRequest(request))) {
			routeMethod(PortalUtil.getUploadPortletRequest(request), response, request)
		} else {
			routeMethod(request, response)
		}
	}

	@PreDestroy
	def void destroy() {
		Logger.info("Destroying %s resources...", getPortletName())
		HibernateFactory.destroy()
	}
}
