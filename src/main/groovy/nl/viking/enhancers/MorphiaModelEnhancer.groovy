package nl.viking.enhancers

import com.google.code.morphia.query.Query
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

    static enhance(Class<? extends Model> type) {
        type.metaClass.'static'.find = {
            MorphiaFactory.ds().find(type)
        }

        type.metaClass.'static'.find = { String keys, Object[] values ->
            Query query = type.find()
            keys.split(",").eachWithIndex { key, i ->
                query.filter(key, values[i])
            }
            query
        }

        type.metaClass.'static'.findAll = {
            type.find().asList()
        }

        type.metaClass.'static'.findById = { String id ->
            if (id) {
                return MorphiaFactory.ds().get(type, new ObjectId(id))
            }
            return null
        }

        type.metaClass.'static'.count = {
            MorphiaFactory.ds().find(type).countAll()
        }

        type.metaClass.'static'.count = { String keys, Object[] values ->
            Query query = type.find()
            keys.split(",").eachWithIndex { key, i ->
                query.filter(key, values[i])
            }
            query.countAll()
        }

    }
}
