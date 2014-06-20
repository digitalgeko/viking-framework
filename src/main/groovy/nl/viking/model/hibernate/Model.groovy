package nl.viking.model.hibernate

import nl.viking.model.liferay.Asset
import nl.viking.model.liferay.AssetInfo
import nl.viking.model.liferay.SocialActivity
import nl.viking.model.liferay.SocialActivityInfo
import org.codehaus.jackson.annotate.JsonIgnore

import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Transient

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */

@MappedSuperclass
class Model extends GenericModel implements Comparable<Model>{

	@Id
	@GeneratedValue
	Long id;

	@Transient @JsonIgnore
	AssetInfo assetInfo

	@Transient @JsonIgnore
	SocialActivityInfo socialActivityInfo

	@Override
	Model save() {
		def obj = super.save()
		if (this.class.isAnnotationPresent(Asset)) {
			this.assetInfo.classPK = this.id
			this.assetInfo.register()
		}
		if (this.class.isAnnotationPresent(SocialActivity)) {
			this.socialActivityInfo.classPK = this.id
			this.socialActivityInfo.register()
		}
		return obj;
	}

	@Override
	def delete() {
		def obj = super.delete()
		if (this instanceof Asset) {
			this.assetInfo.delete()
		}
		if (this instanceof SocialActivity) {
			this.socialActivityInfo.delete()
		}
		return obj;
	}

	@Override
	int compareTo(Model t) {
		return t.id.compareTo(this.id)
	}

	static Model findById(Long id) {
		return null
	}

	static Model findById(String id) {
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

}
