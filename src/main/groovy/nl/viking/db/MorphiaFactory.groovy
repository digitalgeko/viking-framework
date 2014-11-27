package nl.viking.db

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia

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
            def mongo = new MongoClient(GMongoProps.getDBHost(), GMongoProps.getDBPort())
            if (GMongoProps.getDBUsername() != null) {
				def credentials = MongoCredential.createMongoCRCredential(GMongoProps.getDBUsername(), GMongoProps.getDBName(), GMongoProps.getDBPassword().toCharArray())
				mongo.credentialsList.add(credentials)
            }
			dataStore = new Morphia().createDatastore(mongo, GMongoProps.getDBName())
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
				new MongoClient(GMongoProps.getDBHost(), GMongoProps.getDBPort())
			} catch (e) {
				isMongoDefined = false;
			}
		}

		return isMongoDefined
	}

}
