package nl.viking.model.hibernate

import nl.viking.db.HibernateFactory
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


	GenericModel save() {
		HibernateFactory.withSession { Session session ->
			session.saveOrUpdate(this)
		}
		return this
	}

	def delete () {
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

	static Number count() {
		return 0
	}

	static Number count(String keys, Object... values) {
		return 0
	}

}
