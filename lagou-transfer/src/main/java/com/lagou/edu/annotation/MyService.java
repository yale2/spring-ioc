package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * @author yale
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyService {

    String value() default "";

}
