package nl.viking.i18n

import com.liferay.portal.kernel.language.LanguageUtil
import nl.viking.db.MorphiaFactory
import nl.viking.logging.Logger
import nl.viking.model.internal.VikingMessageEntry

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 7/10/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
class Messages {

	Locale locale

	Messages(Locale locale) {
		this.locale = locale
	}

	def get(String key) {
		try {
			def result = null
			if (MorphiaFactory.hasMongo()) {
				result = VikingMessageEntry.get(key, "")?.value ?: VikingMessageEntry.get(key, locale.toString())?.value
			}
			if (!result) {
				result = LanguageUtil.get(locale, key, key)
			}
			if (result) {
				return result
			}

			return key
		} catch (e) {
			Logger.error(e, "Key $key doesn't exists!")
			return key
		}
	}

	def get(String key, Locale locale) {
		try {
			def result = VikingMessageEntry.get(key, locale.toString())?.value

			if (!result) {
				result = LanguageUtil.get(locale, key, key)
			}
			if (result) {
				return result
			}
			return key
		} catch (e) {
			Logger.error(e, "Key $key doesn't exists!")
			return key
		}
	}

	Boolean has(String key) {
		VikingMessageEntry.get(key, "")?.value || VikingMessageEntry.get(key, locale.toString())?.value || LanguageUtil.get(locale, key, "") != ""
	}

	Boolean has(String key, Locale locale) {
		VikingMessageEntry.get(key, locale.toString())?.value || LanguageUtil.get(locale, key, "") != ""
	}

	def get(String key, String... args) {
		String.format(get(key), args)
	}

	def get(String key, Locale locale, String... args) {
		String.format(get(key, locale), args)
	}
}
