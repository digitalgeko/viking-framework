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
		def record = modelClass.findById(assetEntry.classUuid)
		return new VikingModelAssetRenderer(assetEntry: assetEntry, assetRendererAnnotation: assetRendererAnnotation, modelClass: modelClass, record: record)
	}

//	@Override
//	AssetRenderer getAssetRenderer(long groupId, String urlTitle) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
//		return super.getAssetRenderer(groupId, urlTitle)
//	}
//	@Override
//	AssetRenderer getAssetRenderer(long classPK) throws PortalException, com.liferay.portal.kernel.exception.SystemException {
//		def assetEntry = AssetEntryLocalServiceUtil.fetchEntry(modelClass.name, classPK)
//		return new VikingModelAssetRenderer(assetEntry: assetEntry, assetRendererAnnotation: assetRendererAnnotation, modelClass: modelClass)
//	}

	@Override
	String getType() {
		return assetRendererAnnotation.type()
	}

	@Override
	long getClassNameId() {
		return PortalUtil.getClassNameId(className)
	}

	@Override
	String getPortletId() {
		return assetRendererAnnotation.portletId()
	}

}
