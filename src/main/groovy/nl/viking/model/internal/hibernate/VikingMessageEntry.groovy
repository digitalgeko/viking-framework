package nl.viking.model.internal.hibernate

import nl.viking.model.hibernate.Model
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions

import javax.persistence.Entity

/**
 * User: mardo
 * Date: 12/4/13
 * Time: 3:00 PM
 */

@Entity
class VikingMessageEntry extends Model {

	String key

	String locale

	String value

	def saveValue() {
		def entryInDB = get(key, locale) ?: this
		entryInDB.value = this.value
		entryInDB.save()
	}

	static VikingMessageEntry get(String key, String locale) {
		VikingMessageEntry.query { Criteria criteria ->
			criteria.add(Restrictions.eq("key", key))
			criteria.add(Restrictions.eq("locale", locale))
			criteria.uniqueResult()
		}
	}

}
