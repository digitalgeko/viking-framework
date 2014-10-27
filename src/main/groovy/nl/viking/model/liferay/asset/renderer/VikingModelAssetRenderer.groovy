package nl.viking.model.liferay.asset.renderer

import com.liferay.portal.kernel.exception.PortalException
import com.liferay.portal.kernel.exception.SystemException
import com.liferay.portal.kernel.portlet.LiferayPortletRequest
import com.liferay.portal.kernel.portlet.LiferayPortletResponse
import com.liferay.portal.security.permission.PermissionChecker
import com.liferay.portal.util.PortletKeys
import com.liferay.portal.util.WebKeys
import com.liferay.portlet.asset.model.AssetEntry
import com.liferay.portlet.asset.model.BaseAssetRenderer
import nl.viking.logging.Logger
import nl.viking.model.annotation.AssetRenderer
import nl.viking.utils.TemplateUtils

import javax.portlet.PortletRequest
import javax.portlet.PortletURL
import javax.portlet.RenderRequest
import javax.portlet.RenderResponse
import javax.portlet.WindowState

/**
 * User: mardo
 * Date: 10/21/14
 * Time: 7:49 AM
 */
class VikingModelAssetRenderer extends BaseAssetRenderer {

	AssetEntry assetEntry

	AssetRenderer assetRendererAnnotation

	Class modelClass

	@Override
	String getClassName() {
		return modelClass.name
	}

	@Override
	long getClassPK() {
		return assetEntry.classPK
	}

	@Override
	long getGroupId() {
		return assetEntry.groupId
	}

	@Override
	String getSummary(Locale locale) {
		return "summary of my model"
//		return assetEntry.summary
	}

	@Override
	String getTitle(Locale locale) {
		return assetEntry.title
	}

	@Override
	long getUserId() {
		return assetEntry.userId
	}

	@Override
	String getUserName() {
		return assetEntry.userName
	}

	@Override
	String getUuid() {
		return assetEntry.classUuid
	}



	@Override
	String render(RenderRequest renderRequest, RenderResponse renderResponse, String template) throws Exception {
		try {
			if (template.equals(TEMPLATE_ABSTRACT) ||
					template.equals(TEMPLATE_FULL_CONTENT)) {

				def record = modelClass.findById(assetEntry.classUuid)

				def data = [
						record: record
				]

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
				TemplateUtils.writeToOutputStream(assetRendererAnnotation.template(), outputStream, data)
				renderRequest.setAttribute("output", new String(outputStream.toByteArray()))
				return "/html/viking/asset_renderer_output.jsp"
			} else {
				return null;
			}
		} catch (FileNotFoundException e) {
			Logger.error(e, "Asset renderer template for $modelClass not found")
		}
		return null
	}

	@Override
	boolean hasViewPermission(PermissionChecker permissionChecker) throws PortalException, SystemException {
		true
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
			throws PortalException, SystemException {

		return true;
	}


	@Override
	boolean isPrintable() {
		true
	}

	@Override
	boolean isDisplayable() {
		true
	}


	@Override
	String getUrlTitle() {
		return "my-url-title"
	}

	@Override
	PortletURL getURLView(LiferayPortletResponse liferayPortletResponse, WindowState windowState) throws Exception {
		PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(PortletRequest.RENDER_PHASE);
		return portletURL;
	}

	@Override
	String getURLViewInContext(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse, String noSuchEntryRedirect) throws Exception {
		PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(PortletRequest.RENDER_PHASE);
		return portletURL;
	}


	@Override
	PortletURL getURLEdit(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse)
			throws Exception {
		PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(PortletRequest.RENDER_PHASE);
		return portletURL;
	}


}
