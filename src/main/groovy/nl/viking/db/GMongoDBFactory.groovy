package nl.viking.db

import com.gmongo.GMongoClient
import com.mongodb.DB
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/5/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
class GMongoDBFactory {

    static DB db = null

    static DB getDb() {
        if (db == null) {
            def credentials = []
            if (GMongoProps.getDBUsername() != null) {
                credentials << MongoCredential.createCredential(GMongoProps.getDBUsername(), GMongoProps.getDBName(), GMongoProps.getDBPassword().toCharArray())
            }
            def mongo = new GMongoClient(new ServerAddress(GMongoProps.getDBHost(), GMongoProps.getDBPort()), credentials)
            db = mongo.getDB(GMongoProps.getDBName())
        }
        return db
    }

    static destroy() {
        if (db) {
            db.mongo.close()
            db = null
        }
    }
}
