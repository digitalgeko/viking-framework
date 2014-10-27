package nl.viking.model.morphia

import com.google.code.morphia.Datastore
import com.google.code.morphia.annotations.Id
import com.google.code.morphia.annotations.Transient
import com.google.code.morphia.query.Query
import com.liferay.portal.kernel.search.IndexerRegistryUtil
import nl.viking.db.MorphiaFactory
import nl.viking.model.annotation.Searchable
import nl.viking.model.annotation.Asset
import nl.viking.model.liferay.asset.AssetInfo
import nl.viking.model.annotation.SocialActivity
import nl.viking.model.liferay.socialactivity.SocialActivityInfo
import org.bson.types.ObjectId
import org.codehaus.jackson.annotate.JsonIgnore

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

		if (_id) {
			_updated = new Date().getTime()
		} else {
			_created = _updated = new Date().getTime()
		}

		MorphiaFactory.ds().save(this)

		if (this.class.isAnnotationPresent(Asset)) {
			def assetInfo = getAssetInfo()
			assetInfo.classPK = _id.inc
			assetInfo.register()
		}

		if (this.class.isAnnotationPresent(SocialActivity)) {
			def socialActivityInfo = getSocialActivityInfo()
			socialActivityInfo.classPK = _id.inc
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
			assetInfo.classPK = _id.inc
			assetInfo.classUuid = this.id
			assetInfo.delete()
		}

		if (this.class.isAnnotationPresent(SocialActivity)) {
			def socialActivityInfo = getSocialActivityInfo()
			socialActivityInfo.classPK = _id.inc
			socialActivityInfo.delete()
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
			socialActivityInfo = new SocialActivityInfo(this)
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

