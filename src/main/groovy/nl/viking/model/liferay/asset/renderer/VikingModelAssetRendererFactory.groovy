package nl.viking.model.liferay.asset.renderer

import com.liferay.portal.kernel.exception.PortalException
import com.liferay.portal.kernel.portlet.LiferayPortletRequest
import com.liferay.portal.kernel.portlet.LiferayPortletResponse
import com.liferay.portal.security.permission.PermissionChecker
import com.liferay.portal.util.PortalUtil
import com.liferay.portlet.asset.model.AssetEntry
import com.liferay.portlet.asset.model.AssetRenderer
import com.liferay.portlet.asset.model.BaseAssetRendererFactory
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil
import org.omg.CORBA.SystemException

import javax.portlet.PortletRequest
import javax.portlet.PortletURL
import javax.portlet.WindowState

/**
 * User: mardo
 * Date: 10/21/14
 * Time: 7:46 AM
 */
class VikingModelAssetRendererFactory extends BaseAssetRendererFactory {

	Class modelClass

	nl.viking.model.annotation.AssetRenderer assetRendererAnnotation

	VikingModelAssetRendererFactory(Class modelClass, nl.viking.model.annotation.AssetRenderer assetRendererAnnotation) {
		this.modelClass = modelClass
		this.assetRendererAnnotation = assetRendererAnnotation
	}

	@Override
	String getClassName() {
		return modelClass.name
	}

	@Override
	AssetRenderer getAssetRenderer(long classPK, int type) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
		def assetEntry = AssetEntryLocalServiceUtil.fetchEntry(modelClass.name, classPK)
		return new VikingModelAssetRenderer(assetEntry: assetEntry, assetRendererAnnotation: assetRendererAnnotation, modelClass: modelClass)
	}

	@Override
	AssetRenderer getAssetRenderer(long groupId, String urlTitle) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
		return super.getAssetRenderer(groupId, urlTitle)
	}
	@Override
	AssetRenderer getAssetRenderer(long classPK) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
		def assetEntry = AssetEntryLocalServiceUtil.fetchEntry(modelClass.name, classPK)
		return new VikingModelAssetRenderer(assetEntry: assetEntry, assetRendererAnnotation: assetRendererAnnotation, modelClass: modelClass)
	}

	@Override
	AssetEntry getAssetEntry(long assetEntryId) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
		return super.getAssetEntry(assetEntryId)
	}

	@Override
	AssetEntry getAssetEntry(String className, long classPK) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
		return super.getAssetEntry(className, classPK)
	}


	@Override
	String getPortletId() {
		return assetRendererAnnotation.portletId()
	}

	@Override
	String getType() {
		return assetRendererAnnotation.type()
	}

	@Override
	boolean hasPermission(PermissionChecker permissionChecker, long classPK, String actionId) throws Exception {
		true
	}
	@Override
	public boolean isLinkable() {
		return true;
	}

	@Override
	PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
		PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(PortletRequest.RENDER_PHASE);
		return portletURL;
	}


	@Override
	boolean isSelectable() {
		return true
	}

	@Override
	boolean isCategorizable() {
		return true
	}

	@Override
	public PortletURL getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState) {
		PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(PortletRequest.RENDER_PHASE);
		return portletURL;
	}

	@Override
	long getClassNameId() {
		return PortalUtil.getClassNameId(className)
	}


}
