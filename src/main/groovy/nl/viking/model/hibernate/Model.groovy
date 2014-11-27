package nl.viking.model.hibernate

import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil
import com.liferay.portal.service.ServiceContext
import com.liferay.portal.util.PortalUtil
import groovy.json.JsonBuilder
import nl.viking.model.annotation.Asset
import nl.viking.model.annotation.SocialActivity
import nl.viking.model.liferay.asset.AssetInfo
import nl.viking.model.liferay.socialactivity.SocialActivityInfo
import org.codehaus.jackson.annotate.JsonIgnore

import javax.persistence.*

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

	private Date created

	private Date updated

	@Override
	Model save() {
		def obj = super.save()
		return obj;
	}

	@PrePersist @PreUpdate
	void setTimestamps() {
		if (id) {
			updated = new Date()
		} else {
			created = updated = new Date()
		}
	}

	def registerSocialActivity(crudOperation) {
		if (this.class.isAnnotationPresent(SocialActivity)) {
			def socialActivityInfo = getSocialActivityInfo()
			socialActivityInfo.className = this.class.name
			socialActivityInfo.classPK = this.id
			socialActivityInfo.extraData = new JsonBuilder([classUuid: id.toString(), crudOperation: crudOperation]).toString()
			socialActivityInfo.register()
		}
	}

	def registerAsset() {
		if (this.class.isAnnotationPresent(Asset)) {
			def assetInfo = getAssetInfo()
			assetInfo.classPK = this.id
			assetInfo.classUuid = this.id.toString()
			assetInfo.register()

			def workflowHandler = WorkflowHandlerRegistryUtil.getWorkflowHandler(this.class.name)
			if (workflowHandler) {
				if (assetInfo.groupId) {
					WorkflowHandlerRegistryUtil.startWorkflowInstance(PortalUtil.defaultCompanyId, assetInfo.groupId, assetInfo.userId, assetInfo.className, assetInfo.classPK, this, new ServiceContext())
				} else {
					WorkflowHandlerRegistryUtil.startWorkflowInstance(PortalUtil.defaultCompanyId, assetInfo.userId, assetInfo.className, assetInfo.classPK, this, new ServiceContext())
				}
			}
		}
	}
	@PostPersist
	void postPersist() {
		registerAsset()
		registerSocialActivity("create")
	}

	@PostUpdate
	void postUpdate() {
		registerAsset()
		registerSocialActivity("update")
	}

	def unregisterAsset() {
		if (this.class.isAnnotationPresent(Asset)) {
			def assetInfo = getAssetInfo()
			assetInfo.classPK = this.id
			assetInfo.delete()
		}
	}

	@PostRemove
	void postRemove() {
		unregisterAsset()
		registerSocialActivity("delete")
	}

	@Override
	def delete() {
		def obj = super.delete()
		obj
	}

	@Override
	int compareTo(Model t) {
		return t.id.compareTo(this.id)
	}

	public boolean equals(Object other) {
		if (this == other) return true;
		this.id == other.id && this.class == other.class
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
			socialActivityInfo = new SocialActivityInfo()
		}
		return socialActivityInfo
	}

	Date getCreated() {
		return created
	}

	Date getUpdated() {
		return updated
	}
}
