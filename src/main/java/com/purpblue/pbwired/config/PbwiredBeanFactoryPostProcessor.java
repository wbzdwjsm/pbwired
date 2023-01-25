package com.purpblue.pbwired.config;

import com.purpblue.pbwired.util.SpringBeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * Used for load {@link SpringBeanUtils SpringBeanUtils} at first.
 * @author purpblue
 */
public class PbwiredBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(SpringBeanUtils.class);
        ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition("com.purpblue.pbwired.util.SpringBeanUtils", beanDefinition);
        beanFactory.getBean(SpringBeanUtils.class);
    }
}
