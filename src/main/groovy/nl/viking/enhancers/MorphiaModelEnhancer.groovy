package nl.viking.enhancers

import com.google.code.morphia.query.Query
import nl.viking.controllers.Controller
import nl.viking.db.MorphiaFactory
import nl.viking.model.morphia.Model
import org.bson.types.ObjectId

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
class MorphiaModelEnhancer {


	private static addDefaultFilters(Query query) {
		def h = Controller.currentDataHelper
		if (h) {
			query.filter("groupId", h.themeDisplay.scopeGroupId)
			query.filter("companyId", h.themeDisplay.companyId)
		}
		query
	}

    static enhance(Class<? extends Model> type) {
        type.metaClass.'static'.find = {
            def query = MorphiaFactory.ds().find(type)
			addDefaultFilters(query)
        }

        type.metaClass.'static'.find = { String keys, Object[] values ->
            Query query = type.find()
			addDefaultFilters(query)
            keys.split(",").eachWithIndex { key, i ->
                query.filter(key, values[i])
            }
            query
        }

        type.metaClass.'static'.findAll = {
			def query = type.find()
			addDefaultFilters(query)
			query.asList()
        }

		type.metaClass.'static'.findAllInDB = {
			MorphiaFactory.ds().find(type).asList()
		}

        type.metaClass.'static'.findById = { String id ->
            if (id) {
                return MorphiaFactory.ds().get(type, new ObjectId(id))
            }
            return null
        }

        type.metaClass.'static'.count = {
            def query = MorphiaFactory.ds().find(type)
			addDefaultFilters(query)
			query.countAll()
        }

        type.metaClass.'static'.count = { String keys, Object[] values ->
            Query query = type.find()
			addDefaultFilters(query)
            keys.split(",").eachWithIndex { key, i ->
                query.filter(key, values[i])
            }
            query.countAll()
        }

    }
}
