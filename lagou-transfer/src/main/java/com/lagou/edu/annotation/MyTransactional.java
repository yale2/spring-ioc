package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * @author yale
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyTransactional {
}
