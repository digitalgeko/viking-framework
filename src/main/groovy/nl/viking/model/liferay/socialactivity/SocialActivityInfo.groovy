package nl.viking.model.liferay.socialactivity

import com.liferay.portal.kernel.util.StringPool
import com.liferay.portlet.social.service.SocialActivityLocalServiceUtil
import groovy.json.JsonBuilder
import nl.viking.controllers.Controller
import nl.viking.controllers.DataHelper
import nl.viking.model.hibernate.Model

/**
 * User: mardo
 * Date: 10/3/13
 * Time: 3:35 PM
 */
class SocialActivityInfo {

	long userId

	long groupId

	Date createDate = new Date()

	String className

	long classPK

	int type

	String extraData = StringPool.BLANK

	long receiverUserId

	SocialActivityInfo() {
		fill(Controller.currentDataHelper)
	}

	def fill(DataHelper h) {
		if (h) {
			if (h.user) {
				userId = userId ?: h.user.userId
			}
			groupId = groupId ?: h.themeDisplay.scopeGroupId
		}
	}

	def register() {
		fill(Controller.currentDataHelper)
		SocialActivityLocalServiceUtil.addActivity(userId, groupId, createDate, className, classPK, type, extraData, receiverUserId)
	}

	def delete() {
		// SocialActivityLocalServiceUtil.deleteActivities(className, classPK)
	}

}
