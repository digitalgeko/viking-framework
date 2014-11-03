package nl.viking.model.liferay.asset.renderer

import com.liferay.portal.kernel.exception.PortalException
import com.liferay.portal.kernel.portlet.LiferayPortletRequest
import com.liferay.portal.kernel.portlet.LiferayPortletResponse
import com.liferay.portal.kernel.portlet.LiferayWindowState
import com.liferay.portal.kernel.util.WebKeys
import com.liferay.portal.security.permission.ActionKeys
import com.liferay.portal.theme.ThemeDisplay
import com.liferay.portal.util.PortalUtil
import com.liferay.portlet.PortletURLFactoryUtil
import com.liferay.portlet.asset.model.AssetRenderer
import com.liferay.portlet.asset.model.BaseAssetRendererFactory
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil
import nl.viking.logging.Logger
import nl.viking.model.annotation.Asset

import javax.portlet.PortletRequest
import javax.portlet.PortletURL
import javax.portlet.WindowStateException

/**
 * User: mardo
 * Date: 10/21/14
 * Time: 7:46 AM
 */
class VikingModelAssetRendererFactory extends BaseAssetRendererFactory {

	Class modelClass

	Asset assetAnnotation

	VikingModelAssetRendererFactory(Class modelClass, Asset assetAnnotation) {
		this.modelClass = modelClass
		this.assetAnnotation = assetAnnotation
	}

	@Override
	String getClassName() {
		return modelClass.name
	}

	@Override
	AssetRenderer getAssetRenderer(long classPK, int type) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
		def assetEntry = AssetEntryLocalServiceUtil.getEntry(modelClass.name, classPK)
		def record = modelClass.findById(assetEntry.classUuid)
		return new VikingModelAssetRenderer(assetEntry: assetEntry, assetAnnotation: assetAnnotation, modelClass: modelClass, record: record)
	}

	@Override
	String getType() {
		return modelClass.name
	}

	@Override
	long getClassNameId() {
		return PortalUtil.getClassNameId(className)
	}

	@Override
	String getPortletId() {
		return assetAnnotation.portletId()
	}

	@Override
	PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
		def addNewPortletId = assetAnnotation.addNewPortletId()
		ThemeDisplay themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		if (addNewPortletId && themeDisplay.permissionChecker.hasPermission(themeDisplay.scopeGroupId, modelClass.name, null, ActionKeys.ADD_RECORD)) {
			def url = PortletURLFactoryUtil.create(liferayPortletRequest, addNewPortletId, themeDisplay.plid, PortletRequest.RENDER_PHASE);
			try {
				url.setWindowState(LiferayWindowState.MAXIMIZED);
			} catch (WindowStateException wse) {
				Logger.error(wse, "setWindowState problem")
			}

			url.setParameter("className", modelClass.name)

			return url
		}

		return null
	}
}
