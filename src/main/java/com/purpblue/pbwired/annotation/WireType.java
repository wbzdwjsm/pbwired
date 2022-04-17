package com.purpblue.pbwired.annotation;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>WireType that {@link com.purpblue.pbwired.annotation.Pbwired @Pbwired} can choose.
 * <p>Note: If developers want to use {@link Autowired @Autowired} or {@link javax.annotation.Resource @Resource},
 * they should use them directly. {@link com.purpblue.pbwired.annotation.Pbwired @Pbwired} is
 * not designed for replacing them.
 *
 * @author Purpblue
 */
public enum WireType {
    SETTER,

    CONSTRUCTOR
}
