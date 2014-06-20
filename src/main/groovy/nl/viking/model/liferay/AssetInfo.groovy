package nl.viking.model.liferay

import com.liferay.portal.kernel.util.StringPool
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil
import nl.viking.controllers.Controller
import nl.viking.controllers.DataHelper
import nl.viking.model.hibernate.GenericModel
import nl.viking.model.hibernate.Model
import org.codehaus.jackson.annotate.JsonIgnore

/**
 * User: mardo
 * Date: 10/3/13
 * Time: 3:17 PM
 */
class AssetInfo {
	long userId = 0

	long groupId = 0

	String className = null

	long classPK = 0

	String classUuid = null

	long classTypeId = 0

	long[] categoryIds = []

	String[] tagNames = []

	boolean visible = true

	Date startDate = null

	Date endDate = null

	Date publishDate = null

	Date expirationDate = null

	String mimeType = StringPool.BLANK

	String title = StringPool.BLANK

	String description = StringPool.BLANK

	String summary = StringPool.BLANK

	String url = StringPool.BLANK

	String layoutUuid = StringPool.BLANK

	int height = 0

	int width = 0

	Integer priority = null

	boolean sync = false

	AssetInfo() {
		fill(Controller.currentDataHelper)
	}

	AssetInfo(Model model) {
		this.className = model.class.name
		this.classPK = model.id
		fill(Controller.currentDataHelper)
	}

	def fill(DataHelper h) {
		if (h) {
			userId = h.user.userId
			groupId = h.themeDisplay.scopeGroupId
		}
	}

	def register() {
		AssetEntryLocalServiceUtil.updateEntry(userId, groupId, className, classPK, classUuid, classTypeId, categoryIds, tagNames, visible, startDate, endDate, publishDate, expirationDate, mimeType, title, description, summary, url, layoutUuid, height, width, priority, sync)
	}

	def delete() {
		AssetEntryLocalServiceUtil.deleteEntry(className, classPK)
	}

	@JsonIgnore
	boolean isVisible() {
		return visible
	}

	@JsonIgnore
	boolean isSync() {
		return sync
	}
}
