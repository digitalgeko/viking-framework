package nl.viking.model.morphia

import nl.viking.db.GMongoDBFactory
import org.bson.types.ObjectId

/**
 * User: mardo
 * Date: 11/28/13
 * Time: 10:07 AM
 */
class SimpleCollection {

	String name

	SimpleCollection(String name) {
		this.name = name
	}

	def getDbCollection() {
		GMongoDBFactory.db[name]
	}

	def methodMissing(String name, args) {
		def queryMatcher = name =~ /(find|findAll|count)By(.*)/

		if (name == "findById") {
			return dbCollection.findOne(new ObjectId(args[0]))
		}

		if (queryMatcher) {
			def captures = queryMatcher[0]
			def func = captures[1]
			def fieldNames = captures[2].split("And")

			def i = 0
			def fieldsObj = fieldNames.collectEntries {
				[(it): args[i++]]
			}

			switch (func) {
				case "find":
					return dbCollection.findOne(fieldsObj)

				case "findAll":
					return dbCollection.find(fieldsObj)

				case "count":
					return dbCollection.find(fieldsObj).count()
			}
		}

		dbCollection.invokeMethod(name, args)
	}

	static $static_propertyMissing(String name) {
		new SimpleCollection(name)
	}

}

class Coll {
	static $static_propertyMissing(String name) {
		new SimpleCollection(name)
	}
}