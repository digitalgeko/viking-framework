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

	def record

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
		TemplateUtils.i18nTemplate(locale, "model.resource.${modelClass.name}.description", [record:record])
	}

	@Override
	String getTitle(Locale locale) {
		TemplateUtils.i18nTemplate(locale, "model.resource.${modelClass.name}.title", [record:record])
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


}
