package com.purpblue.pbwired.env;

import com.purpblue.pbwired.annotation.FinalInject;
import com.purpblue.pbwired.util.ValueResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * <p>The annotation - {@link FinalInject @FinalInject}, takes effect by using Spring's {@link Environment Environment}.
 * This class sets Spring's Environment to {@link ValueResolver ValueResolver}
 * which processes the "key" attribute in {@link FinalInject @FinalInject}.
 *
 * <p>So, developers should <strong>NOT</strong> use @FinalInject in Boot Class({@link SpringBootApplication @SpringBootApplication}-annotated class)
 * for doing this will lead to forward use of Spring's env.
 *
 * <p>Just like {@link org.springframework.beans.factory.annotation.Autowired @Autowired} and {@link org.springframework.beans.factory.annotation.Value @Value},
 * developers also should <em>NOT</em> use {@link com.purpblue.pbwired.annotation.FinalInject @FinalInject}
 * in any {@link EnvironmentPostProcessor EnvironmentPostProcessor}.
 *
 * @see ValueResolver
 * @author Purpblue
 */
public class FinalInjectEnvPostProcessor implements EnvironmentPostProcessor, Ordered {

    public static final int DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 1;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        ValueResolver.setEnv(environment);
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
}
