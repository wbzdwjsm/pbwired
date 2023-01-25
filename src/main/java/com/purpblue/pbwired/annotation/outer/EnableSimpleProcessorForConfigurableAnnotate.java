package com.purpblue.pbwired.annotation.outer;

import com.purpblue.pbwired.config.PbwiredBeanFactoryPostProcessor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Be annotated onto configuration class to enable {@link PbwiredBeanFactoryPostProcessor PbwiredBeanFactoryPostProcessor}.
 * If not, {@link Configurable Configurable} will not work unless Spring's LoadTimeWeaving(LTW) is used.
 *
 * @author purpblue
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PbwiredBeanFactoryPostProcessor.class)
public @interface EnableSimpleProcessorForConfigurableAnnotate {
}
