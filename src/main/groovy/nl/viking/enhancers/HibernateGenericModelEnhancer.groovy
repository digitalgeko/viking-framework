package nl.viking.enhancers

import nl.viking.controllers.Controller
import nl.viking.db.HibernateFactory
import nl.viking.model.hibernate.GenericModel
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions

import javax.persistence.EntityManager
import javax.persistence.Query

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
class HibernateGenericModelEnhancer {

	private static addDefaultFilters(Criteria criteria) {
		def h = Controller.currentDataHelper
		if (h) {
			criteria.add(Restrictions.eq("companyId", h.themeDisplay.companyId))
		}
		criteria
	}
    static enhance(Class<? extends GenericModel> type) {

		type.metaClass.'static'.find = { String whereStr = null, Map<String, Object> values = [:] ->
			EntityManager em = HibernateFactory.currentEntityManager
			def queryStr = " from $type.simpleName "
			if (whereStr) {
				queryStr += " where $whereStr "
			}
			def query = em.createQuery( queryStr, type )
			values.each {
				query.setParameter(it.key, it.value)
			}
			query
		}

		type.metaClass.'static'.findAll = {
			type.find().resultList
		}

		type.metaClass.'static'.findAllInDB = {
			type.find().resultList
		}

		type.metaClass.'static'.count = { String whereStr = null, Map<String, Object> values = [:] ->
			Query query = type.find(whereStr, values)
			query.singleResult
		}

    }
}
