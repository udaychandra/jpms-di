/*
 * Copyright (c) 2018 UCK Inc.
 */

package ud.jpms.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Reference {
    String name() default "";

    Cardinality cardinality() default Cardinality.ONE;
}
