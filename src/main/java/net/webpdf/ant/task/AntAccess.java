package net.webpdf.ant.task;

import java.lang.annotation.*;

/**
 * Marks ANT interface methods (create, add, getter, setter)
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface AntAccess {
}
