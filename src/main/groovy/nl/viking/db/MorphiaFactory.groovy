package nl.viking.db

import com.google.code.morphia.Datastore
import com.google.code.morphia.Morphia
import com.mongodb.Mongo


/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 10:12 AM
 * To change this template use File | Settings | File Templates.
 */
class MorphiaFactory {
    
    static Datastore dataStore = null

	static Boolean isMongoDefined

    static Datastore ds() {
        if (dataStore == null  && GMongoProps.getDBHost()) {
            def mongo = new Mongo(GMongoProps.getDBHost(), GMongoProps.getDBPort())
            if (GMongoProps.getDBUsername() != null) {
                dataStore = new Morphia().createDatastore(mongo, GMongoProps.getDBName(), GMongoProps.getDBUsername(), GMongoProps.getDBPassword().toCharArray())
            } else {
                dataStore = new Morphia().createDatastore(mongo, GMongoProps.getDBName())
            }
        }
        return dataStore
    }

	static Boolean hasMongo() {
		return this.isMongoDefined;
	}

	static Boolean getIsMongoDefined() {
		if (isMongoDefined == null) {
			isMongoDefined = true;
			try {
				new Mongo(GMongoProps.getDBHost(), GMongoProps.getDBPort())
			} catch (e) {
				isMongoDefined = false;
			}
		}

		return isMongoDefined
	}

}
