package com.purpblue.pbwired.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.annotation.*;

/**
 * <p>Annotate a class for constant injection.
 * If needed, uses {@link DefaultValue @DefaultValue} to set the default value for the field
 * in {@link String String}, no matter what type the field is.
 * <p>A typical use is as follows:
 * <pre>
 *    {@literal @}ConstantClass
 *     public class SomeConstant {
 *         public static final String CONSTANT_0;
 *
 *         public static final Integer CONSTANT_1;
 *
 *         public static final List&lt;String&gt; CONSTANT_2;
 *
 *         public static final Map&lt;String, Object&gt; CONSTANT_3;
 *     }
 * </pre>
 * <p>Values set in .properties/.yml config files will be injected into the fields,
 * using their names as the key. Also, "prefix" attribute is accepted as in
 * {@link ConfigurationProperties @ConfigurationProperties}.
 *
 * <p>If key-value pairs have not been set in .properties/.yml config files in advance,
 * {@link DefaultValue @DefaultValue} can be used for avoiding {@code null} or {@code Exception}.
 * <p>For example:
 * <pre>
 *     {@literal @}ConstantClass
 *      public class SomeConstant2 {
 *
 *         {@literal @}DefaultValue("3.14, 2.71, 0.99")
 *          public static final double[] NUM_ARRAY;
 *      }
 * </pre>
 *
 * @see DefaultValue
 * @author Purpblue
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ConstantClass {

    /**
     * The name prefix of the properties, just like the "prefix" attribute in
     * {@link ConfigurationProperties @ConfigurationProperties}.
     */
    String prefix() default "";

    /**
     * Fields modified simultaneously by these modifiers will be injected values.
     * @return Modifiers which fields are modified with will be processed.
     */
    String[] modifiersInclude() default {"static", "final"};

    /**
     * Field names that will not be injected.
     * @return Fields that will <string>NOT</string> be processed.
     */
    String[] fieldsExclude() default {};

}
