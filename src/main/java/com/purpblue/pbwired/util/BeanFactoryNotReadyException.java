package com.purpblue.pbwired.util;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * Value resolving depends on {@link DefaultListableBeanFactory DefaultListableBeanFactory}.
 * If it does not exist in classpath or has not initialized yet, all resolving cannot work
 * and leads to this exception.
 *
 * @author Purpblue
 */
public class BeanFactoryNotReadyException extends RuntimeException {

    public BeanFactoryNotReadyException() {}

    public BeanFactoryNotReadyException(String message) {
        super(message);
    }

    public BeanFactoryNotReadyException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
