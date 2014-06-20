package nl.viking.model.gmongo

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.gridfs.GridFSDBFile
import nl.viking.db.GMongoDBFactory
import org.bson.types.ObjectId

class GMongoModel implements Comparable<GMongoModel> {

    String _id

    private def getDBProps() {
        this.properties.findAll { !['class', 'metaClass', 'mongo', 'db', '_id'].contains(it.key) && it.value != null && !it.key.startsWith("__") && !(it.value instanceof List || it.value instanceof LinkedHashMap)}.each {
            if (it.getValue() instanceof GMongoModel) {
                GMongoModel model = (GMongoModel) it.getValue()
                it.setValue(["id": new ObjectId(model._id), "ref": model.getClass().getCanonicalName()])
            } else if (it.getValue() instanceof GridFSDBFile) {
                GridFSDBFile gridfsFile = (GridFSDBFile) it.getValue()
                it.setValue(["id": gridfsFile._id, "gridref": "files"])
            } else if (it.getValue() instanceof Enum) {
                Enum enumValue = it.getValue()
                it.setValue(enumValue.name())
            }
        }
    }

    static def convertDBProps(Map props) {
        props.each {
            if (it.getValue() instanceof BasicDBObject) {
                BasicDBObject obj = it.getValue()
                if (obj.containsField("id") && obj.containsField("ref")){
                    it.setValue(Class.forName(obj.get("ref")).findById(obj.get("id")))
                }
            }
            if (it.getKey() == "_id") {
                it.setValue(it.getValue().toString())
            }
        }
        props
    }

    def dbRef() {
        [id: new ObjectId(this._id), ref:getClass().getCanonicalName()]
    }


    GMongoModel save() {
        def props = getDBProps()
        if (_id){
            this.col().update(['_id': new ObjectId(this._id)], [$set: props])
        }else{
            this.col() << props
            this._id = props._id.toString()
        }
        return this
	}

    def delete () {
        this.col().remove("_id": new ObjectId(this._id))
    }

    public static String getCollectionNameForClass(Class clazz) {
        clazz.getSimpleName()
    }

    public static DBCollection getCollectionForClass(Class clazz){
        def db = GMongoDBFactory.getDb()
        db[getCollectionNameForClass(clazz)]
    }

    @Override
    int compareTo(GMongoModel t) {
        return t._id.compareTo(this._id)
    }

}