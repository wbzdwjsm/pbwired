package com.purpblue.pbwired.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * ValueResolver for constants, based on {@link DefaultListableBeanFactory DefaultListableBeanFactory}.
 *
 * @see DefaultListableBeanFactory
 * @author Purpblue
 */
public class ValueResolver {
    private static BeanFactory beanFactory;

    public static void setBeanFactory(BeanFactory beanFactory) {
        ValueResolver.beanFactory = beanFactory;
    }

    public static <T> T getProperty(Class<?> klazz, String varName) {
        if (beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
            try {
                return (T) defaultListableBeanFactory.doResolveDependency(
                    new DependencyDescriptor(klazz.getDeclaredField(varName), true, true),
                    null,
                    null,
                    null
                );
            } catch (NoSuchFieldException e) {
                // ignore
            }
        }
        throw new BeanFactoryNotReadyException("org.springframework.beans.factory.support.DefaultListableBeanFactory does not exist in classpath, or has not initialized yet. Do NOT use @ConstantClass or @FinalInject in Boot Class. The variable Name: " + varName);
    }

}
