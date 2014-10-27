package nl.viking.model.hibernate

import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil
import com.liferay.portal.service.ServiceContext
import com.liferay.portal.service.WorkflowInstanceLinkLocalServiceUtil
import com.liferay.portal.util.PortalUtil
import nl.viking.model.annotation.Asset
import nl.viking.model.liferay.asset.AssetInfo
import nl.viking.model.annotation.SocialActivity
import nl.viking.model.liferay.socialactivity.SocialActivityInfo
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

	private Date created

	private Date updated

	@Override
	Model save() {

		if (id) {
			updated = new Date()
		} else {
			created = updated = new Date()
		}

		def obj = super.save()

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

		if (this.class.isAnnotationPresent(SocialActivity)) {
			def socialActivityInfo = getSocialActivityInfo()
			socialActivityInfo.classPK = this.id
			socialActivityInfo.register()
		}


		return obj;
	}

	@Override
	def delete() {
		def obj = super.delete()

		if (this.class.isAnnotationPresent(Asset)) {
			def assetInfo = getAssetInfo()
			assetInfo.classPK = this.id
			assetInfo.delete()
		}

		if (this.class.isAnnotationPresent(SocialActivity)) {
			def socialActivityInfo = getSocialActivityInfo()
			socialActivityInfo.classPK = this.id
			socialActivityInfo.delete()
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

	Date getCreated() {
		return created
	}

	Date getUpdated() {
		return updated
	}
}
