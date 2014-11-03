package nl.viking.model.liferay.socialactivity

import com.liferay.portal.kernel.language.LanguageUtil
import com.liferay.portal.model.User
import com.liferay.portal.service.UserLocalServiceUtil
import com.liferay.portal.theme.ThemeDisplay
import com.liferay.portlet.social.model.BaseSocialActivityInterpreter
import com.liferay.portlet.social.model.SocialActivityFeedEntry
import groovy.json.JsonSlurper
import nl.viking.model.annotation.SocialActivity
import nl.viking.utils.ReflectionUtils
import nl.viking.utils.TemplateUtils

/**
 * User: mardo
 * Date: 10/5/13
 * Time: 10:19 AM
 */
class VikingActivityInterpreter extends BaseSocialActivityInterpreter{

	@Override
	String[] getClassNames() {
		ReflectionUtils.getModelClassNamesWithAnnotations(SocialActivity.class)
	}

	@Override
	protected SocialActivityFeedEntry doInterpret(com.liferay.portlet.social.model.SocialActivity activity, ThemeDisplay themeDisplay) throws Exception {
		User user = UserLocalServiceUtil.getUser(activity.userId)
		User receiverUser = activity.receiverUserId ? UserLocalServiceUtil.getUser(activity.receiverUserId) : null
		String link = "#"
		def extraDataJson = new JsonSlurper().parseText(activity.extraData)
		def modelClass = Class.forName(activity.className)
		def record = modelClass.findById(extraDataJson.classUuid)
		def templateData = [
		        user: user,
				activity: activity,
				record: record,
				extraData: extraDataJson,
				receiverUser: receiverUser,
		]

		def titleKey = "model.resource.${activity.className}.${extraDataJson.crudOperation}.title"
		def descriptionKey = "model.resource.${activity.className}.${extraDataJson.crudOperation}.description"

		def modelClassString = LanguageUtil.isValidLanguageKey(themeDisplay.locale, "model.resource.${activity.className}") ? LanguageUtil.get(themeDisplay.locale, "model.resource.${activity.className}") : modelClass.simpleName
		def defaultMessages = [
				create: "${user.firstName} created a new ${modelClassString}",
				update: "${user.firstName} updated a new ${modelClassString}",
				delete: "${user.firstName} deleted a new ${modelClassString}",
		]
		String title = LanguageUtil.isValidLanguageKey(themeDisplay.locale, titleKey) ? TemplateUtils.i18nTemplate(themeDisplay.locale, titleKey, templateData) : defaultMessages[extraDataJson.crudOperation]
		String body = LanguageUtil.isValidLanguageKey(themeDisplay.locale, titleKey) ? TemplateUtils.i18nTemplate(themeDisplay.locale, descriptionKey, templateData) : ""

		return new SocialActivityFeedEntry(link, title, body)
	}

}
