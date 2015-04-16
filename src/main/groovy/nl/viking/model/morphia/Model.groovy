package nl.viking.model.morphia

import com.liferay.portal.kernel.search.IndexerRegistryUtil
import com.liferay.portlet.asset.model.AssetEntry
import groovy.json.JsonBuilder
import nl.viking.controllers.Controller
import nl.viking.db.MorphiaFactory
import nl.viking.model.annotation.Asset
import nl.viking.model.annotation.Searchable
import nl.viking.model.annotation.SearchableField
import nl.viking.model.annotation.SocialActivity
import nl.viking.model.liferay.asset.AssetInfo
import nl.viking.model.liferay.socialactivity.SocialActivityInfo
import nl.viking.utils.MongoUtils
import org.bson.types.ObjectId
import org.codehaus.jackson.annotate.JsonIgnore
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Transient
import org.mongodb.morphia.query.Query

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
class Model implements Comparable<Model> {

    @Id ObjectId _id

    @Transient
    private String id

    private long _created

    private long _updated

    @SearchableField
	Long groupId

    @SearchableField
	Long companyId

	@Transient @JsonIgnore
	AssetInfo assetInfo

	@Transient @JsonIgnore
	SocialActivityInfo socialActivityInfo

    String getId() {
        if (_id) {
            return _id.toString()
        }
        return null
    }

    Model save() {
		def h = Controller.currentDataHelper
		if (h) {
            this.groupId = this.properties.groupId ?: h.themeDisplay.scopeGroupId
            this.companyId = this.properties.companyId ?: h.themeDisplay.companyId
		}

		def crudOperation
		if (_id) {
			crudOperation = "update"
			_updated = new Date().getTime()
		} else {
			crudOperation = "create"
			_created = _updated = new Date().getTime()
		}

		MorphiaFactory.ds().save(this)

		if (this.class.isAnnotationPresent(Asset)) {
			def assetInfo = getAssetInfo()
			assetInfo.classPK = MongoUtils.objectIdToLong(_id)
			assetInfo.classUuid = _id.toString()
			assetInfo.register()
		}

		if (this.class.isAnnotationPresent(SocialActivity)) {
			def socialActivityInfo = getSocialActivityInfo()
			socialActivityInfo.className = this.class.name
			socialActivityInfo.classPK = MongoUtils.objectIdToLong(_id)
			socialActivityInfo.extraData = new JsonBuilder([classUuid: _id.toString(), crudOperation: crudOperation]).toString()
			socialActivityInfo.register()
		}

		if (this.class.isAnnotationPresent(Searchable)) {
			def indexer = IndexerRegistryUtil.getIndexer(this.class)
			indexer.reindex(this)
		}

		return this
    }

    def delete () {
		MorphiaFactory.ds().delete(this)

		if (this.class.isAnnotationPresent(Asset)) {
			def assetInfo = getAssetInfo()
			assetInfo.classPK = MongoUtils.objectIdToLong(_id)
			assetInfo.classUuid = _id.toString()
			assetInfo.delete()
		}

		if (this.class.isAnnotationPresent(SocialActivity)) {
			def socialActivityInfo = getSocialActivityInfo()
			socialActivityInfo.className = this.class.name
			socialActivityInfo.classPK = MongoUtils.objectIdToLong(_id)
			socialActivityInfo.extraData = new JsonBuilder([classUuid: _id.toString(), crudOperation: "delete"]).toString()
			socialActivityInfo.register()
		}

		if (this.class.isAnnotationPresent(Searchable)) {
			def indexer = IndexerRegistryUtil.getIndexer(this.class)
			indexer.delete(this)
		}

		return this
    }

    static Datastore ds() {
        return MorphiaFactory.ds()
    }

    @Override
    int compareTo(Model t) {
        return t._id.compareTo(this._id)
    }

	// The following methods will be implemented by meta programming
    static Query find() {
        return null
    }

    static Query find(String keys, Object... values) {
        return null
    }

    static List findAll() {
        return null
    }

	static List findAllInDB() {
		return null
	}

    static Model findById(String id) {
        return null
    }

    static long count() {
        return 0
    }

    static long count(String keys, Object... values) {
        return 0
    }

	static Model fromJson (json) {
		return null
	}

	// fields
	AssetInfo getAssetInfo() {
		if (!assetInfo) {
			assetInfo = new AssetInfo(this)
		}
		return assetInfo
	}

	SocialActivityInfo getSocialActivityInfo() {
		if (!socialActivityInfo) {
			socialActivityInfo = new SocialActivityInfo()
		}
		return socialActivityInfo
	}

	Date getCreated() {
		new Date(_created)
	}

	Date getUpdated() {
		new Date(_updated)
	}
}

