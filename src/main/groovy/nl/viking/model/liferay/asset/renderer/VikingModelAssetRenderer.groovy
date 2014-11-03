package nl.viking.model.liferay.asset.renderer

import com.liferay.portal.kernel.language.LanguageUtil
import com.liferay.portal.kernel.portlet.LiferayPortletRequest
import com.liferay.portal.kernel.portlet.LiferayPortletResponse
import com.liferay.portal.kernel.portlet.LiferayWindowState
import com.liferay.portal.kernel.util.WebKeys
import com.liferay.portal.theme.ThemeDisplay
import com.liferay.portlet.PortletURLFactoryUtil
import com.liferay.portlet.asset.model.AssetEntry
import com.liferay.portlet.asset.model.BaseAssetRenderer
import nl.viking.logging.Logger
import nl.viking.model.annotation.Asset
import nl.viking.utils.TemplateUtils

import javax.portlet.PortletRequest
import javax.portlet.PortletURL
import javax.portlet.RenderRequest
import javax.portlet.RenderResponse
import javax.portlet.WindowState
import javax.portlet.WindowStateException

/**
 * User: mardo
 * Date: 10/21/14
 * Time: 7:49 AM
 */
class VikingModelAssetRenderer extends BaseAssetRenderer {

	AssetEntry assetEntry

	Asset assetAnnotation

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
						record: record,
						template: template
				]

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
				TemplateUtils.writeToOutputStream(assetAnnotation.template(), outputStream, data)
				renderRequest.setAttribute("viking_model_render_output", new String(outputStream.toByteArray()))
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
	PortletURL getURLView(LiferayPortletResponse liferayPortletResponse, WindowState windowState) throws Exception {
		return super.getURLView(liferayPortletResponse, windowState)
	}

	@Override
	String getURLViewInContext(LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse, String noSuchEntryRedirect) throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		def url = PortletURLFactoryUtil.create(liferayPortletRequest, assetAnnotation.portletId(), themeDisplay.plid, PortletRequest.RENDER_PHASE);
		try {
			url.setWindowState(LiferayWindowState.MAXIMIZED);
		} catch (WindowStateException wse) {
			Logger.error(wse, "setWindowState problem")
		}

		url.setParameter("className", modelClass.name)
		url.setParameter("classPK", record.id.toString())

		return url.toString()
	}

}
