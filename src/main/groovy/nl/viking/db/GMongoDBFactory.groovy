package nl.viking.db

import com.gmongo.GMongoClient
import com.mongodb.DB
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import nl.viking.logging.Logger

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/5/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
class GMongoDBFactory {

    static DB db = null

    static GMongoClient mongo = null

    static DB getDb() {
        if (db == null) {
            def credentials = []
            if (GMongoProps.getDBUsername() != null) {
                credentials << MongoCredential.createCredential(GMongoProps.getDBUsername(), GMongoProps.getDBName(), GMongoProps.getDBPassword().toCharArray())
            }
			def serverAddresses = GMongoProps.getDBServerAddresses()
			if (serverAddresses) {
                List<ServerAddress> seeds = []

                for (String t : serverAddresses) {
                    t = t.replace("[", "").replace("]","")
                    def cleanString = t.split(":")
                    def host = cleanString[0].toString() as String
                    def port = cleanString.length > 1 ? cleanString[1] as int : null
                    def seed = new ServerAddress(host, port ?: GMongoProps.getDBPort())
                    seeds.add(seed)
                }
                mongo = new GMongoClient(seeds, credentials)

			} else {
				mongo = new GMongoClient(new ServerAddress(GMongoProps.getDBHost(), GMongoProps.getDBPort()), credentials)
			}

            db = mongo.getDB(GMongoProps.getDBName())
        }
        return db
    }

    static destroy() {
        if (mongo) {
            try {
                mongo.close()
            } catch (e) {
                Logger.warn("Mongo client was not successfully closed")
            } finally {
                mongo = null
            }
        }
    }
}
