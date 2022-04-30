package com.purpblue.pbwired.annotation;

import java.lang.annotation.*;

/**
 * Works with {@link ConstantClass @ConstantClass} to set the default value for fields.
 * The default value is held in {@link String String}, no matter what type the field is.
 *
 * @see ConstantClass
 * @author Purpblue
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface DefaultValue {

    String value();
}
