package nl.viking.i18n

import com.liferay.portal.kernel.language.LanguageUtil
import nl.viking.db.MorphiaFactory
import nl.viking.logging.Logger
import nl.viking.model.internal.VikingMessageEntry

import javax.portlet.PortletConfig

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 7/10/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
class Messages {

	ResourceBundle resourceBundle

	PortletConfig portletConfig

	Messages(PortletConfig portletConfig, Locale locale) {
		this.portletConfig = portletConfig
		this.resourceBundle = portletConfig.getResourceBundle(locale)
	}

	def get(String key) {
		try {
			def result = null
			if (MorphiaFactory.hasMongo()) {
				result = VikingMessageEntry.get(key, "")?.value ?: VikingMessageEntry.get(key, Locale.default.toString())?.value
			}
			if (!result && resourceBundle.containsKey(key)) {
				result = resourceBundle.getString(key)
			}
			if (!result) {
				result = LanguageUtil.get(Locale.default, key, key)
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
			def localeResourceBundle = portletConfig.getResourceBundle(locale)
			if (!result && localeResourceBundle.containsKey(key)) {
				result = localeResourceBundle.getString(key)
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

	Boolean has(String key) {
		VikingMessageEntry.get(key, "")?.value || VikingMessageEntry.get(key, Locale.default.toString())?.value || resourceBundle.containsKey(key) || LanguageUtil.get(Locale.default, key, "") != ""
	}

	Boolean has(String key, Locale locale) {
		VikingMessageEntry.get(key, locale.toString())?.value || portletConfig.getResourceBundle(locale).containsKey(key) || LanguageUtil.get(locale, key, "") != ""
	}

	def get(String key, String... args) {
		String.format(get(key), args)
	}

	def get(String key, Locale locale, String... args) {
		String.format(get(key, locale), args)
	}
}
