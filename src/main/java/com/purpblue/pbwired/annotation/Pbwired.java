/*
 * Copyright(c) 2020 Purpblue. All Rights Reserved
 */
package com.purpblue.pbwired.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Many developers like using constructors for injection because of its strong dependency, and many others like setter injection,
 * but they don't like the annoying constructor or setter codes. Now they may try {@link com.purpblue.pbwired.annotation.Pbwired Pbwired}.
 *
 * Note: when @Pbwired and @Autowired/@Resource exist on the same field, @Pbwired will be ignored.
 *
 * For use, see https://github.com/wbzdwjsm/pbwired
 *
 * For value injection, you can use @Pbvalue.
 *
 * @see Pbvalue
 * @author Purpblue
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Pbwired {
    /**
     * To indicate in which way for injection
     * Kindly note: because of java's mechanism, using constructors for injection
     * cannot solve the classic circular reference problem! In this situation,
     * you can use setters for injection, or use @Autowired/@Resource.
     * @return
     */
    WireType wireType() default WireType.CONSTRUCTOR;

    /**
     * Bean name to be used for injection
     * @return
     */
    String name() default "";
}
