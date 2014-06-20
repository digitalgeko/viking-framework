package nl.viking.model.liferay

import com.liferay.portal.model.User
import com.liferay.portal.service.UserLocalServiceUtil
import com.liferay.portal.theme.ThemeDisplay
import com.liferay.portlet.social.model.BaseSocialActivityInterpreter
import com.liferay.portlet.social.model.SocialActivityFeedEntry
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
			Set<Class<?>> modelClasses = reflections.getTypesAnnotatedWith(SocialActivity.class);
			return modelClasses.collect{ it.name } as String[]
		}
		return new String[0]  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected SocialActivityFeedEntry doInterpret(com.liferay.portlet.social.model.SocialActivity activity, ThemeDisplay themeDisplay) throws Exception {
		User user = UserLocalServiceUtil.getUser(activity.userId)
		String link = "#"
		String title = "${user.firstName} created a $activity.extraData"
		String body = formatter.format(activity.createDate)

		return new SocialActivityFeedEntry(link, title, body);
	}

}
