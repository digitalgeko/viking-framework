package nl.viking.controllers.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/3/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD])
public @interface Render {
	String mode();
}