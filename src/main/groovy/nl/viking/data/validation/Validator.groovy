package nl.viking.data.validation

import nl.viking.controllers.DataHelper
import nl.viking.i18n.ContextualMessageInterpolatorLocale

import javax.validation.Configuration
import javax.validation.Validation
import javax.validation.ValidatorFactory

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
class Validator {

	private javax.validation.Validator validator;

	DataHelper h

    LinkedHashMap<String,String> errors = []

    Validator(DataHelper h) {
		this.h = h

		Configuration configuration = Validation.byDefaultProvider().configure();
		def messageInterpolator = new ContextualMessageInterpolatorLocale(configuration.getDefaultMessageInterpolator(), h)

		ValidatorFactory factory = configuration.messageInterpolator(messageInterpolator).buildValidatorFactory();
		validator = factory.getValidator();
    }

    def validate(String key, Object value) {
        def valueErrors = this.validator.validate(value)
        if (valueErrors.size() > 0) {
            valueErrors.each {
				addError(key+"."+it.propertyPath, it.message)
            }
        }
    }

	def addError(String key, String errorKey) {
		if (!errors[key]) {
			errors[key] = h.messages.get(errorKey)
		}
	}

    def hasErrors() {
        errors.size() > 0
    }
}
