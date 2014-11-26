package nl.viking.utils

import org.bson.types.ObjectId

import java.nio.ByteBuffer

/**
 * User: mardo
 * Date: 11/14/14
 * Time: 11:07 AM
 */
class MongoUtils {

	static long objectIdToLong(ObjectId _id) {
		ByteBuffer.wrap(_id.toByteArray()).getLong(4)
	}

}
