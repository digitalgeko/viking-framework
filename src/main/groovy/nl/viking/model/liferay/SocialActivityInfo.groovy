package nl.viking.model.liferay

import com.liferay.portal.kernel.util.StringPool
import com.liferay.portlet.social.service.SocialActivityLocalServiceUtil
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

	}

	SocialActivityInfo(Model model) {
		this.className = model.class.name
		this.extraData = model.class.simpleName
		if (model.id) {
			this.classPK = model.id
		}
	}

	def fill(DataHelper h) {
		userId = h.user.userId
		groupId = h.themeDisplay.scopeGroupId
	}

	def register() {
		SocialActivityLocalServiceUtil.addActivity(userId, groupId, createDate, className, classPK, type, extraData, receiverUserId)
	}

	def delete() {
		SocialActivityLocalServiceUtil.deleteActivities(className, classPK)
	}

}
