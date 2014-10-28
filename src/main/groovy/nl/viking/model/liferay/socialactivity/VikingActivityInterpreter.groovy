package nl.viking.model.liferay.socialactivity

import com.liferay.portal.model.User
import com.liferay.portal.service.UserLocalServiceUtil
import com.liferay.portal.theme.ThemeDisplay
import com.liferay.portlet.social.model.BaseSocialActivityInterpreter
import com.liferay.portlet.social.model.SocialActivityFeedEntry
import nl.viking.model.annotation.SocialActivity
import nl.viking.utils.TemplateUtils
import org.reflections.Reflections

import java.text.SimpleDateFormat

/**
 * User: mardo
 * Date: 10/5/13
 * Time: 10:19 AM
 */
class VikingActivityInterpreter extends BaseSocialActivityInterpreter{

	def formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")

	@Override
	String[] getClassNames() {
		if (VikingActivityInterpreter.getClassLoader().getResource("models") != null){
			def reflections = new Reflections("models")
			Set<Class<?>> modelClasses = reflections.getTypesAnnotatedWith(SocialActivity.class)
			return modelClasses.collect{ it.name } as String[]
		}
		return new String[0]
	}

	@Override
	protected SocialActivityFeedEntry doInterpret(com.liferay.portlet.social.model.SocialActivity activity, ThemeDisplay themeDisplay) throws Exception {
		User user = UserLocalServiceUtil.getUser(activity.userId)
		String link = "#"
		def record = Class.forName(activity.className).findById(activity.extraData)
		def templateData = [
		        user: user,
				activity: activity,
				record: record
		]

		String title = TemplateUtils.i18nTemplate(themeDisplay.locale, "model.resource.${activity.className}.title", templateData) ?: "${user.firstName} created a $activity.extraData"
		String body = TemplateUtils.i18nTemplate(themeDisplay.locale, "model.resource.${activity.className}.description", templateData) ?: formatter.format(activity.createDate)

		return new SocialActivityFeedEntry(link, title, body)
	}

}
