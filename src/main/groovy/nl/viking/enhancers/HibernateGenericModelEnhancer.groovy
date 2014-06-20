package nl.viking.enhancers

import nl.viking.db.HibernateFactory
import nl.viking.model.hibernate.GenericModel
import org.hibernate.Criteria
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
class HibernateGenericModelEnhancer {

    static enhance(Class<? extends GenericModel> type) {
		type.metaClass.'static'.query = { closure ->
			HibernateFactory.withSession { Session session ->
				closure(session.createCriteria(type))
			}
		}

		type.metaClass.'static'.find = { String keys, Object[] values ->
			type.query { Criteria criteria ->
				keys.split(",").eachWithIndex { key, i ->
					criteria.add(Restrictions.eq(key, values[i]))
				}
				criteria.list()
			}
		}

		type.metaClass.'static'.findAll = {
			type.query { Criteria criteria ->
				criteria.list()
			}
		}

		type.metaClass.'static'.count = {
			type.query { Criteria criteria ->
				criteria.setProjection(Projections.rowCount()).uniqueResult();
			}
		}

		type.metaClass.'static'.count = { String keys, Object[] values ->
			type.find(keys,values).setProjection(Projections.rowCount()).uniqueResult();
		}
    }
}
