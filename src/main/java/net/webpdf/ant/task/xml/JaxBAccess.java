package net.webpdf.ant.task.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks JAXB interface methods
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@interface JaxBAccess {
}
