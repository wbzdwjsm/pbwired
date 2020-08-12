/*
 * Copyright(c) 2020 Purpblue. All Rights Reserved
 */
package com.purpblue.pbwired.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used for static and non-static value injection,
 * but for non-static value injection, you'd better
 * use {@link org.springframework.beans.factory.annotation.Value Value} instead.
 *
 * Note: when @Pbvalue and @Value exist on the same field, @Pbvalue will be ignored.
 *
 * For use, see https://github.com/wbzdwjsm/pbwired
 *
 * For field injection, see {@link com.purpblue.pbwired.annotation.Pbwired Pbwired}
 *
 * @author Purpblue
 * @see Pbwired
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface Pbvalue {
    String value();
}
