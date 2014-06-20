package nl.viking.model.gmongo.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/10/13
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
public @interface Binary {

}