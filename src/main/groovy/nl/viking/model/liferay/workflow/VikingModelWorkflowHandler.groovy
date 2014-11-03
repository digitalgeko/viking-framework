package nl.viking.model.liferay.workflow

import com.liferay.portal.kernel.exception.PortalException
import com.liferay.portal.kernel.exception.SystemException
import com.liferay.portal.kernel.language.LanguageUtil
import com.liferay.portal.kernel.portlet.LiferayPortletRequest
import com.liferay.portal.kernel.portlet.LiferayPortletResponse
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler
import com.liferay.portal.security.permission.ResourceActions
import com.liferay.portal.security.permission.ResourceActionsUtil
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil
import nl.viking.model.annotation.Workflow
import nl.viking.utils.TemplateUtils

import javax.portlet.PortletURL

/**
 * User: mardo
 * Date: 10/22/14
 * Time: 3:22 PM
 */
class VikingModelWorkflowHandler extends BaseWorkflowHandler {

	Class modelClass

	Workflow workflowAnnotation

	VikingModelWorkflowHandler(Class modelClass, Workflow workflowAnnotation) {
		this.modelClass = modelClass
		this.workflowAnnotation = workflowAnnotation
	}

	@Override
	String getClassName() {
		return modelClass.name
	}

	@Override
	String getTitle(long classPK, Locale locale) {
		def assetEntry = AssetEntryLocalServiceUtil.getEntry(modelClass.name, classPK)
		if (assetEntry && workflowAnnotation.title()) {
			def record = modelClass.findById(assetEntry.classUuid)
			def data = [
					record: record
			]
			return TemplateUtils.i18nTemplate(locale, workflowAnnotation.title(), data)
		}
		getType(locale)
	}

	@Override
	PortletURL getURLEdit(long classPK, LiferayPortletRequest liferayPortletRequest, LiferayPortletResponse liferayPortletResponse)
			throws Exception {

		return null;
	}

	@Override
	String getType(Locale locale) {
		return LanguageUtil.get(locale, ResourceActionsUtil.modelResourceNamePrefix+className)
	}

	@Override
	Object updateStatus(int status, Map<String, Serializable> workflowContext) throws PortalException, SystemException {
		return null
	}

}
