package nl.viking.model.liferay.asset

import com.liferay.portal.kernel.util.StringPool
import com.liferay.portlet.asset.NoSuchEntryException
import com.liferay.portlet.asset.model.AssetEntry
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil
import nl.viking.controllers.Controller
import nl.viking.controllers.DataHelper
import nl.viking.model.hibernate.Model
import nl.viking.utils.MongoUtils
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

	Date createDate = null

	Date modifiedDate = null

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
        fill()
	}

	AssetInfo(Model model) {
		this.className = model.class.name
		if (model.id) {
			this.classPK = model.id
			this.classUuid = model.id.toString()
		}
        this.groupId = model.groupId
        fill()
	}

	AssetInfo(nl.viking.model.morphia.Model model) {
		this.className = model.class.name
		if (model.id) {
			this.classPK = MongoUtils.objectIdToLong(model._id)
			this.classUuid = model.id
		}
        this.groupId = model.groupId

        fill()
	}

    def fill() {
        def assetEntry = getAssetEntry()
        if (assetEntry) {
            fillWithAssetEntry(assetEntry)
        }else{
            fillWithH(Controller.currentDataHelper)
        }
    }

	def fillWithH(DataHelper h) {
		if (h) {
			if (h.user) {
				userId = userId ?: h.user.userId
			}
			groupId = groupId ?: h.themeDisplay.scopeGroupId
		}
	}

    def getAssetEntry() {
        try {
            return AssetEntryLocalServiceUtil.getEntry(className, classPK)
        } catch (NoSuchEntryException e) {
            return null
        }
    }

    def fillWithAssetEntry(AssetEntry assetEntry) {
        this.userId = assetEntry.userId
        this.groupId = assetEntry.groupId
        this.className = assetEntry.className
        this.classPK = assetEntry.classPK
        this.classUuid = assetEntry.classUuid
        this.classTypeId = assetEntry.classTypeId
        this.categoryIds = assetEntry.categoryIds
        this.tagNames = assetEntry.tagNames
        this.visible = assetEntry.visible
        this.createDate = assetEntry.createDate
        this.modifiedDate = assetEntry.modifiedDate
        this.startDate = assetEntry.startDate
        this.endDate = assetEntry.endDate
        this.publishDate = assetEntry.publishDate
        this.expirationDate = assetEntry.expirationDate
        this.mimeType = assetEntry.mimeType
        this.title = assetEntry.title
        this.description = assetEntry.description
        this.summary = assetEntry.summary
        this.url = assetEntry.url
        this.layoutUuid = assetEntry.layoutUuid
        this.height = assetEntry.height
        this.width = assetEntry.width
        this.priority = assetEntry.priority
    }

	def register() {
		fillWithH(Controller.currentDataHelper)
		AssetEntryLocalServiceUtil.updateEntry(userId, groupId, createDate, modifiedDate, className, classPK, classUuid, classTypeId, categoryIds, tagNames, visible, startDate, endDate, expirationDate, mimeType, title, description, summary, url, layoutUuid, height, width, priority, sync)
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
