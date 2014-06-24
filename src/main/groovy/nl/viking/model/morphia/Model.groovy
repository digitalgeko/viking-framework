package nl.viking.model.morphia

import com.google.code.morphia.Datastore
import com.google.code.morphia.annotations.Id
import com.google.code.morphia.annotations.Transient
import com.google.code.morphia.query.Query
import nl.viking.db.MorphiaFactory
import org.bson.types.ObjectId

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
class Model implements Comparable<Model> {

    @Id ObjectId _id;

    @Transient
    private String id

    long _created

    long _updated

    String getId() {
        if (_id) {
            return _id.toString()
        }
        return null
    }

    Model save() {
        MorphiaFactory.ds().save(this)
        return this
    }

    def delete () {
        MorphiaFactory.ds().delete(this)
    }

    static Datastore ds() {
        return MorphiaFactory.ds()
    }

    @Override
    int compareTo(Model t) {
        return t.id.compareTo(this.id)
    }

    Date getCreated() {
        new Date(_created)
    }

    Date getUpdated() {
        new Date(_updated)
    }

// The following methods will be implemented by meta programming
    static Query find() {
        return null
    }

    static Query find(String keys, Object... values) {
        return null
    }

    static List findAll() {
        return null
    }

    static Model findById(String id) {
        return null
    }

    static long count() {
        return 0
    }

    static long count(String keys, Object... values) {
        return 0
    }

	static Model fromJson (json) {
		return null
	}
}

