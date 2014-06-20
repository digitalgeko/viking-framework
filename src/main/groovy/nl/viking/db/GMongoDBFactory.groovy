package nl.viking.db

import com.gmongo.GMongo
import com.mongodb.DB

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
            def mongo = new GMongo(GMongoProps.getDBHost(), GMongoProps.getDBPort())
            db = mongo.getDB(GMongoProps.getDBName())
            if (GMongoProps.getDBUsername() != null) {
                db.authenticate(GMongoProps.getDBUsername(), GMongoProps.getDBPassword().toCharArray());
            }
        }
        return db
    }
}
