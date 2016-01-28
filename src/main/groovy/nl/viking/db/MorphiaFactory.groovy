package nl.viking.db

import com.gmongo.GMongoClient
import com.mongodb.Mongo
import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia

import javax.annotation.PreDestroy

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
			List<MongoCredential> credentialsList = []
			if (GMongoProps.getDBUsername() != null) {
				def credentials = MongoCredential.createCredential(GMongoProps.getDBUsername(), GMongoProps.getDBName(), GMongoProps.getDBPassword().toCharArray())
				credentialsList.add(credentials)
			}

			def mongo
			def serverAddresses = GMongoProps.getDBServerAddresses()
			if (serverAddresses) {
				def seeds = serverAddresses.collect {
					new ServerAddress(it.host, it.port ?: GMongoProps.getDBPort())
				}
				mongo = new GMongoClient(seeds, credentialsList)
			} else {
				mongo = new MongoClient(new ServerAddress(GMongoProps.getDBHost(), GMongoProps.getDBPort()), credentialsList)
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

	static destroy() {
		if (dataStore) {
			dataStore.mongo.close()
			dataStore = null
		}

	}

}
