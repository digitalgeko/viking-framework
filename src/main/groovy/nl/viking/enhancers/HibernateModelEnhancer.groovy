package nl.viking.enhancers

import nl.viking.db.HibernateFactory
import nl.viking.db.MorphiaFactory
import nl.viking.model.hibernate.Model
import org.bson.types.ObjectId
import org.hibernate.Criteria
import org.hibernate.Query
import org.hibernate.Session
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions

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
				return type.query { Criteria criteria ->
					criteria.add(Restrictions.eq("id", id)).uniqueResult()
				}
			}
			return null
		}
		type.metaClass.'static'.findById = { String id ->
			if (id && id.isNumber()) {
				return type.findById(new Long(id))
			}
			return null
		}
    }
}
