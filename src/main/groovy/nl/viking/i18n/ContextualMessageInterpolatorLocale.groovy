package nl.viking.i18n

import nl.viking.controllers.DataHelper

import javax.validation.MessageInterpolator

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 7/25/13
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
class ContextualMessageInterpolatorLocale implements MessageInterpolator {
	
	final MessageInterpolator delegate;
	final DataHelper h;

	ContextualMessageInterpolatorLocale(MessageInterpolator delegate, DataHelper h) {
		this.delegate = delegate
		this.h = h
	}

	@Override
	String interpolate(String messageTemplate, MessageInterpolator.Context context) {
		def key = getKey(messageTemplate)
		return h.messages.get(key);
	}

	@Override
	String interpolate(String messageTemplate, MessageInterpolator.Context context, Locale locale) {
		def key = getKey(messageTemplate)
		if (h.messages.has(key, locale)) {
			return h.messages.get(key, locale);
		} else {
			return this.delegate.interpolate(messageTemplate, context, this.h.locale);
		}
	}

	String getKey(String messageTemplate) {
		def m = messageTemplate =~ (/\{(.*)\}/)
		m[0][1]
	}
	
}
