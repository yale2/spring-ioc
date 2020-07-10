package com.lagou.edu.annotation;

import com.sun.istack.internal.NotNull;

import java.lang.annotation.*;

/**
 * @author yale
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyQualifier {

    @NotNull
    String value();
}
