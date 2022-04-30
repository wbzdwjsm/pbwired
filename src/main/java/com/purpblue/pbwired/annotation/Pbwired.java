/*
 * Copyright(c) 2020 Purpblue. All Rights Reserved
 */
package com.purpblue.pbwired.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Many developers like using constructors for injection because of its strong dependency, and many others like setter injection,
 * but they all don't like the annoying constructor or setter codes. Now they may try {@link com.purpblue.pbwired.annotation.Pbwired Pbwired}.
 *
 * <p>Note: When {@link com.purpblue.pbwired.annotation.Pbwired Pbwired} and {@link Autowired Autowired}/{@link javax.annotation.Resource Resource}
 * exist on the same field, @Pbwired will be ignored.
 * When compiled, @Pbwired will be transformed into @Autowired.
 * <p>For use, see https://github.com/wbzdwjsm/pbwired
 *
 * <p>For value injection, developers can use {@link Pbvalue Pbvalue}.
 *
 * @see Pbvalue
 * @author Purpblue
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface Pbwired {
    /**
     * <p>To indicate in which way for injection, default is {@link WireType#CONSTRUCTOR CONSTRUCTOR}.
     * <p>Note: Because of java's mechanism, using constructors for injection
     * cannot solve the classic circular reference problem. In this situation,
     * developers can use {@link WireType#SETTER SETTER} for injection,
     * or use {@link Autowired @Autowired}/{@link javax.annotation.Resource @Resource} directly.
     */
    WireType wireType() default WireType.CONSTRUCTOR;

    /**
     * Bean name to be used for injection, equivalent to the "name" attribute of
     * {@link javax.annotation.Resource @Resource}
     */
    String name() default "";
}
