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
 * Assuming your class is named "NameServiceImpl":
 * <p>For static value injection:
 * You can write code as follows:
 * <blockquote>
 *     @Pbvalue("${me.name}")
 *     private static String name;
 * </blockquote>
 *
 * When compiled, your .class file looks like:
 * <blockquote>
 *     private static String name;
 *
 *     @Value("${me.name}")
 *     public void setName(String name) {
 *         NameServiceImpl.name = name;
 *     }
 * </blockquote>
 *
 * <p>For non-static fields' injection:
 * In this situation, @Pbvalue is fully equals to @Value.
 * You can write code as follows:
 * <blockquote>
 *     @Pbvalue("${me.name}")
 *     private String name;
 * </blockquote>
 *
 * When compiled, your .class file looks like:
 * <blockquote>
 *     private String name;
 *
 *     @Value("${me.name}")
 *     public void setName(String name) {
 *         this.name = name;
 *     }
 * </blockquote>
 * </p>
 *
 * For fields injection, you can use {@link com.purpblue.pbwired.annotation.Pbwired Pbwired}
 *
 * @author Purpblue
 * @see Pbwired
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface Pbvalue {
    String value();
}
