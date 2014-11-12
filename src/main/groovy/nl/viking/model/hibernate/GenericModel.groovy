package nl.viking.model.hibernate

import com.liferay.portal.kernel.search.IndexerRegistryUtil
import nl.viking.controllers.Controller
import nl.viking.db.HibernateFactory
import nl.viking.model.annotation.Searchable
import org.hibernate.Session

import javax.persistence.MappedSuperclass

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

		def h = Controller.currentDataHelper
		if (h) {
			groupId = groupId ?: h.themeDisplay.scopeGroupId
			companyId = companyId ?: h.themeDisplay.companyId
		}

		HibernateFactory.withSession { Session session ->
			session.saveOrUpdate(this)
		}

		if (this.class.isAnnotationPresent(Searchable)) {
			def indexer = IndexerRegistryUtil.getIndexer(this.class)
			indexer.reindex(this)
		}

		return this
	}

	def delete () {

		if (this.class.isAnnotationPresent(Searchable)) {
			def indexer = IndexerRegistryUtil.getIndexer(this.class)
			indexer.delete(this)
		}

		HibernateFactory.withSession { Session session ->
			session.delete(this)
		}

	}

	static def query(Closure closure) {
		return null
	}

	static List<Model> find(String keys, Object... values) {
		return null
	}

	static List findAll() {
		return null
	}

	static List findAllInDB() {
		return null
	}

	static Number count() {
		return 0
	}

	static Number count(String keys, Object... values) {
		return 0
	}

}
