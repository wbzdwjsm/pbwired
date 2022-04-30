package com.purpblue.pbwired.annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotate a <strong>final</strong> field for value injection in Spring Framework,
 * even including <strong>static final</strong> field.
 *
 * <p>A typical use for annotating a final field with {@link FinalInject @FinalInject}
 * is as following, no matter whether the field is <strong>static</strong> or not:
 * <blockquote><pre>
 * {@code @FinalInject(key = "constant.0")
 *    public static final String CONSTANT_0;
 * }
 * </pre></blockquote>
 *
 * <p>A colon is accepted for default value just like {@link Value @Value}, for example:
 * <blockquote><pre>
 * {@code @FinalInject(key = "constant.1:CONSTANT1")
 *    public final String CONSTANT_1;
 * }
 * </pre></blockquote>
 *
 * <p>Here the "key" attribute in {@link FinalInject @FinalInject} is equivalent to
 * the "value" attribute in {@link Value @Value}.
 * This means developers should configure the key-value pairs in .properties/.yml files,
 * just as using {@link Value @Value}.
 *
 * <p>Kindly note: Developers can use {@link FinalInject @FinalInject} in many classes, including POJO and
 * those not annotated with {@link Component @Component}, {@link Service @Service}, {@link Configuration @Configuration}, etc.
 * But they should <strong>never</strong> use {@link FinalInject @FinalInject} in
 * Boot Class({@link SpringBootApplication @SpringBootApplication}-annotated class) or any
 * {@link EnvironmentPostProcessor EnvironmentPostProcessor}. Doing so will produce NPE for
 * forward use of Spring's {@link Environment Environment}.
 *
 * @author Purpblue
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface FinalInject {

    /**
     * The "key"(or "value") attribute is highly similar to the "value" attribute in {@link Value @Value}.
     * @return The key configured in Spring's config files which linked to the value that developers want to use,
     *         accepting a colon as the separator for default value.
     *         EL expression is accepted.
     */
    @AliasFor("value")
    String key() default "";

    /**
     * @see #key()
     */
    @AliasFor("key")
    String value() default "";
}
