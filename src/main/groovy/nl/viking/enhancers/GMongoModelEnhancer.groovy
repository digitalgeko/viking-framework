package nl.viking.enhancers

import com.google.code.morphia.query.Query
import nl.viking.db.MorphiaFactory
import nl.viking.model.gmongo.GMongoModel
import nl.viking.model.morphia.Model
import org.bson.types.ObjectId

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
class GMongoModelEnhancer {

    static enhance(Class<? extends GMongoModel> type) {
        type.metaClass.'static'.col = {
            GMongoModel.getCollectionForClass(type)
        }
        type.metaClass.'static'.findById = { id ->
            if (id != null && !id.toString().isEmpty()) {
                if (id instanceof String) {
                    id = new ObjectId(id)
                }
                Map props = GMongoModel.getCollectionForClass(type).findOne(_id: id)
                if (props) {
                    return GMongoModel.convertDBProps(props)
                }
            }
            return null
        }
        type.metaClass.'static'.find = { args ->
            def results
            if (args){
                results = type.col().find(args)
            }else{
                results = type.col().find()
            }
            results
        }
        type.metaClass.'static'.findAll = { args ->
            def results
            if (args){
                results = type.col().find(args)
            }else{
                results = type.col().find()
            }

            results.toList().collect{
                GMongoModel.convertDBProps(it).asType(type)
            }
        }
        type.metaClass.'static'.findOne = { args ->
            def result
            if (args) {
                result = GMongoModel.getCollectionForClass(type).findOne(args)
            }else{
                result = GMongoModel.getCollectionForClass(type).findOne()
            }
            if (result == null) {
                return null
            }
            GMongoModel.convertDBProps(result).asType(type)
        }
    }
}
