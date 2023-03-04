package com.purpblue.pbwired.util;

import com.purpblue.pbwired.annotation.outer.EnableSimpleProcessorForConfigurableAnnotate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Map;

/**
 * Works at runtime, depends on {@link EnableSimpleProcessorForConfigurableAnnotate @EnableSimpleConfigurableProcess}.
 * Used for resolving {@link Autowired @Autowired} or {@link Resource @Resource}.
 * Developers can also inject {@link ApplicationContext}, {@link BeanFactory}, {@link HttpServletRequest}, etc.,
 * which is the same as in {@link Component}-annotated beans.
 * @author purpblue
 */
public class SpringBeanUtils implements BeanFactoryAware {

    private static BeanFactory beanFactory;

    /**
     * Reference to DefaultListableBeanFactory#resolvableDependencies
     */
    private static Map<Class<?>, Object> resolvableDependencies;

    public static <T> T getBean(String beanName) {
        return beanFactory == null ? null : (T) beanFactory.getBean(beanName);
    }

    public static <T> T getBean(Class<T> klazz) {
        if (beanFactory == null) {
            return null;
        }
        // Copied from org.springframework.beans.factory.support.DefaultListableBeanFactory#findAutowireCandidates
        for (Map.Entry<Class<?>, Object> classObjectEntry : resolvableDependencies.entrySet()) {
            Class<?> autowiringType = classObjectEntry.getKey();
            if (autowiringType.isAssignableFrom(klazz)) {
                Object autowiringValue = classObjectEntry.getValue();
                autowiringValue = resolveAutowiringValue(autowiringValue, klazz);
                if (klazz.isInstance(autowiringValue)) {
                    return (T) autowiringValue;
                }
            }
        }
        return beanFactory.getBean(klazz);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpringBeanUtils.beanFactory = beanFactory;
        try {
            Field resolvableDependenciesField = DefaultListableBeanFactory.class.getDeclaredField("resolvableDependencies");
            resolvableDependenciesField.setAccessible(true);
            resolvableDependencies = (Map<Class<?>, Object>) resolvableDependenciesField.get(beanFactory);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new BeanFactoryNotReadyException("DefaultListableBeanFactory not ready. Is this Springboot?");
        }
    }

    /**
     * Copied from org.springframework.beans.factory.support.AutowireUtils#resolveAutowiringValue(java.lang.Object, java.lang.Class)
     */
    private static <T> Object resolveAutowiringValue(Object autowiringValue, Class<T> requiredType) {
        if (autowiringValue instanceof ObjectFactory && !requiredType.isInstance(autowiringValue)) {
            ObjectFactory<?> factory = (ObjectFactory<?>) autowiringValue;
            if (autowiringValue instanceof Serializable && requiredType.isInterface()) {
                autowiringValue = Proxy.newProxyInstance(requiredType.getClassLoader(),
                    new Class<?>[]{requiredType}, new ObjectFactoryDelegatingInvocationHandler(factory));
            } else {
                return factory.getObject();
            }
        }
        return autowiringValue;
    }

    /**
     * Copied from org.springframework.beans.factory.support.AutowireUtils.ObjectFactoryDelegatingInvocationHandler.
     */
    private static class ObjectFactoryDelegatingInvocationHandler implements InvocationHandler, Serializable {

        private final ObjectFactory<?> objectFactory;

        ObjectFactoryDelegatingInvocationHandler(ObjectFactory<?> objectFactory) {
            this.objectFactory = objectFactory;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals":
                    // Only consider equal when proxies are identical.
                    return (proxy == args[0]);
                case "hashCode":
                    // Use hashCode of proxy.
                    return System.identityHashCode(proxy);
                case "toString":
                    return this.objectFactory.toString();
            }
            try {
                return method.invoke(this.objectFactory.getObject(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}
