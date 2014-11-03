package nl.viking.controllers

import com.liferay.portal.kernel.util.JavaConstants
import com.liferay.portal.kernel.util.WebKeys
import com.liferay.portal.model.User
import com.liferay.portal.service.ServiceContext
import com.liferay.portal.service.ServiceContextFactory
import com.liferay.portal.theme.ThemeDisplay
import com.liferay.portal.util.PortalUtil
import nl.viking.i18n.Messages
import nl.viking.logging.Logger

import javax.portlet.PortletConfig
import javax.portlet.PortletSession
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/22/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
class DataHelper {

    def request

    def response

	def portletRequest

    ThemeDisplay themeDisplay

    HttpServletRequest servletRequest

	HttpServletResponse servletResponse

    Locale locale

    User user

    String portletId

    PortletSession session

	Messages messages

	PortletConfig portletConfig

	ServiceContext serviceContext

	DataHelper(request, response, portletRequest) {
		this.request = request
		this.response = response
		this.portletRequest = portletRequest
	}

	Messages getMessages() {
		if (!messages) {
			messages = new Messages(getLocale())
		}
		return messages
	}

	ServiceContext getServiceContext() {
		if (!serviceContext) {
			serviceContext = ServiceContextFactory.getInstance(portletRequest)
		}
		return serviceContext
	}

	PortletConfig getPortletConfig() {
		if (!portletConfig) {
			portletConfig = (PortletConfig) portletRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		}
		return portletConfig
	}

    User getUser() {
        if (user == null) {
            user = (User) portletRequest.getAttribute(WebKeys.USER);
        }
        return user
    }

    ThemeDisplay getThemeDisplay() {
        if (themeDisplay == null) {
            themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
        }
        return themeDisplay
    }

    HttpServletRequest getServletRequest() {
        if (servletRequest == null) {
            servletRequest = PortalUtil.getHttpServletRequest(portletRequest);
        }
        return servletRequest
    }

	HttpServletResponse getServletResponse() {
		if (servletResponse == null) {
			servletResponse = PortalUtil.getHttpServletResponse(response);
		}
		return servletResponse
	}

    Locale getLocale() {
        if (locale == null) {
            if (getUser()) {
                locale = getUser().locale;
            }else{
                locale = getThemeDisplay().locale
            }
        }
        return locale
    }

    String getPortletId() {
        if (portletId == null) {
            portletId = (String)portletRequest.getAttribute(WebKeys.PORTLET_ID);
        }
        return portletId
    }

    PortletSession getSession() {
        if (session == null) {
            session = portletRequest.getPortletSession()
        }
        return session
    }

    String getContextPath() {
        this.portletRequest.contextPath
    }

	boolean hasPermission(resourceName, actionId, groupId = null, primKey = null) {
		def themeDisplay = getThemeDisplay()
		if (!groupId) {
			groupId = themeDisplay.siteGroupId
		}
		themeDisplay.permissionChecker.hasPermission(groupId, resourceName, primKey, actionId)
	}
}
