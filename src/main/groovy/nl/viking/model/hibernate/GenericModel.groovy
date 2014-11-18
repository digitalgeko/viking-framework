package nl.viking.model.hibernate

import com.google.code.morphia.annotations.PostPersist
import com.liferay.portal.kernel.search.IndexerRegistryUtil
import nl.viking.controllers.Controller
import nl.viking.db.HibernateFactory
import nl.viking.model.annotation.Searchable
import org.hibernate.Session

import javax.persistence.Query
import javax.persistence.EntityManager
import javax.persistence.MappedSuperclass
import javax.persistence.PostRemove
import javax.persistence.PostUpdate
import javax.persistence.PrePersist

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 7/15/13
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
class GenericModel {

	Long groupId

	Long companyId

	GenericModel save() {
		HibernateFactory.withEntityManager { EntityManager em ->
			em.persist(this)
		}
		return this
	}

	@PrePersist
	void prePersist() {
		setDefaultFields()
	}

	def setDefaultFields() {
		def h = Controller.currentDataHelper
		if (h) {
			groupId = groupId ?: h.themeDisplay.scopeGroupId
			companyId = companyId ?: h.themeDisplay.companyId
		}
	}

	@PostPersist @PostUpdate
	void postPersist() {
		registerSearchIndex()
	}

	def registerSearchIndex() {
		if (this.class.isAnnotationPresent(Searchable)) {
			def indexer = IndexerRegistryUtil.getIndexer(this.class)
			indexer.reindex(this)
		}
	}
	def unregisterSearchIndex() {
		if (this.class.isAnnotationPresent(Searchable)) {
			def indexer = IndexerRegistryUtil.getIndexer(this.class)
			indexer.delete(this)
		}
	}

	@PostRemove
	void postRemove() {
		unregisterSearchIndex()
	}

	def delete () {
		HibernateFactory.withEntityManager { EntityManager em ->
			em.remove(this)
		}
	}

	static Query find(String whereStr = null, Map<String, Object> values = [:]) {
		return null
	}

	static List findAll() {
		return null
	}

	static List findAllInDB() {
		return null
	}

	static Number count(String whereStr = null, Map<String, Object> values = [:]) {
		return 0
	}

}
