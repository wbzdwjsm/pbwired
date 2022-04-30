package com.purpblue.pbwired.env;

import com.purpblue.pbwired.annotation.FinalInject;
import com.purpblue.pbwired.util.ValueResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.Ordered;

/**
 * <p>The annotation - {@link FinalInject @FinalInject}, takes effect by using {@link DefaultListableBeanFactory DefaultListableBeanFactory}.
 * This class sets BeanFactory to {@link ValueResolver ValueResolver}.
 *
 * <p>So, developers should <strong>NOT</strong> use {@link FinalInject @FinalInject} in Boot Class({@link SpringBootApplication @SpringBootApplication}-annotated class)
 * for the forward use of {@link DefaultListableBeanFactory DefaultListableBeanFactory}.
 *
 * @see ValueResolver
 * @author Purpblue
 */
public class FinalInjectBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    public static final int DEFAULT_ORDER = HIGHEST_PRECEDENCE;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ValueResolver.setBeanFactory(beanFactory);
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
}
