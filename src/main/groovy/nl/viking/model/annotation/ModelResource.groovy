package nl.viking.model.annotation

import com.liferay.portal.security.permission.ActionKeys

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * User: mardo
 * Date: 10/15/14
 * Time: 9:59 AM
 */

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
public @interface ModelResource {

	String[] portlets() default []

	String weight() default "100"

	String[] supports () default ["ADD_RECORD", "VIEW", "UPDATE", "DELETE"]

	String[] siteMemberDefaults() default ["VIEW"]

	String[] guestDefaults() default ["VIEW"]

	String[] guestUnsupported () default []

}