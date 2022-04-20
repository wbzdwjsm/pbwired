/*
 * Copyright(c) 2020 Purpblue. All Rights Reserved
 */
package com.purpblue.pbwired.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation can be used for <strong>static and non-static</strong> value injection,
 * but for non-static value injection, developers had better
 * use {@link org.springframework.beans.factory.annotation.Value @Value} instead.
 *
 * <p>Note: When @Pbvalue and @Value exist on the same field, @Pbvalue will be ignored.
 *
 * <p>For use, see https://github.com/wbzdwjsm/pbwired.
 *
 * <p>For field injection, developers can use {@link com.purpblue.pbwired.annotation.Pbwired @Pbwired}.
 *
 * @see Pbwired
 * @author Purpblue
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface Pbvalue {
    String value();
}
