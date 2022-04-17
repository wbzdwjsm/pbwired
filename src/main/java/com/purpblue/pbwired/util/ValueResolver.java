package com.purpblue.pbwired.util;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;

/**
 * ValueResolver for final fields, based on {@link Environment} and
 * {@link ConversionService}.
 *
 * @author Purpblue
 */
public class ValueResolver {
    private static Environment env;

    public static void setEnv(Environment env) {
        ValueResolver.env = env;
    }

    public static <T> T getProperty(String key, Class<T> varType, String varName) {
        if (key == null || "".equals(key)) {
            throw new IllegalArgumentException("@FinalInject: var: " + varName + ": key must NOT be empty String.");
        }
        if (env == null) {
            throw new NullPointerException("DO NOT use @FinalInject in Boot Class(@SpringBootApplication-annotated class) or EnvironmentPostProcessor! Doing this will lead to NPE for Environment has not been initialized yet!");
        }
        String[] keyAndDefaultValue = key.split(":");
        T result = env.getProperty(keyAndDefaultValue[0], varType);
        if (result != null) {
            return result;
        }
        if (keyAndDefaultValue.length == 1) {
            throw new IllegalArgumentException("@FinalInject: var: " + varName + ". Cannot resolve key: " + key + ". Have you forgotten to set the value in configs?");
        }
        ConversionService conversionService = ApplicationConversionService.getSharedInstance();
        return conversionService.convert(keyAndDefaultValue[1], varType);
    }
}
