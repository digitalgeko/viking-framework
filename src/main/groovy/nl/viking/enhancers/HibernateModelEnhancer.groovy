package nl.viking.enhancers

import nl.viking.db.HibernateFactory
import nl.viking.model.hibernate.Model
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions

import javax.persistence.EntityManager

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
class HibernateModelEnhancer {

    static enhance(Class<? extends Model> type) {

		type.metaClass.'static'.findById = { Long id ->
			if (id) {
				EntityManager em = HibernateFactory.currentEntityManager
				return em.find(type, id)
			}
			return null
		}

		type.metaClass.'static'.findById = { String id ->
			if (id && id.isNumber()) {
				return type.findById(id as Long)
			}
			return null
		}

    }
}
