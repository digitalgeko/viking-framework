package nl.viking.db

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import nl.viking.logging.Logger
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
			List<MongoCredential> credentialsList = []
			if (GMongoProps.getDBUsername() != null) {
				def credentials = MongoCredential.createCredential(GMongoProps.getDBUsername(), GMongoProps.getDBName(), GMongoProps.getDBPassword().toCharArray())
				credentialsList.add(credentials)
			}

			def mongo
			def serverAddresses = GMongoProps.getDBServerAddresses()
			if (serverAddresses) {
				Logger.debug "serverAddresses value $serverAddresses"
				def seeds = serverAddresses.collect {
					def cleanString = it.split(":")
					def host = cleanString[0].toString()
					def port = cleanString.length > 1 ? cleanString[1] as int : null
					new ServerAddress(host, port ?: GMongoProps.getDBPort())
				}
				mongo = new MongoClient(seeds, credentialsList)
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
