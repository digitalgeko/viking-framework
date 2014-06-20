package nl.viking.model.liferay

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * User: mardo
 * Date: 10/5/13
 * Time: 9:54 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
public @interface Asset {

}