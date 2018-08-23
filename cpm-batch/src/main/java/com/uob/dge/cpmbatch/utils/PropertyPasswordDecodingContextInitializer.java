package com.uob.dge.cpmbatch.utils;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PropertyPasswordDecodingContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Pattern decodePasswordPattern = Pattern.compile("uobpassword\\((.*?)\\)");

    private PasswordDecoder passwordDecoder = new PasswordDecoder();

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            Map<String, Object> propertyOverrides = new LinkedHashMap<>();
            decodePasswords(propertySource, propertyOverrides, environment);
            if (!propertyOverrides.isEmpty()) {
                PropertySource<?> decodedProperties = new MapPropertySource("decoded " + propertySource.getName(), propertyOverrides);
                environment.getPropertySources().addBefore(propertySource.getName(), decodedProperties);
            }
        }
    }

    private void decodePasswords(PropertySource<?> source, Map<String, Object> propertyOverrides, ConfigurableEnvironment environment) {
        if (source instanceof EnumerablePropertySource) {
            EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) source;
            for (String key : enumerablePropertySource.getPropertyNames()) {
                Object rawValue = source.getProperty(key);
                if (rawValue instanceof String) {
                    String decodedValue = decodePasswordsInString((String) rawValue, environment);
                    propertyOverrides.put(key, decodedValue);
                }
            }
        }
    }

    private String decodePasswordsInString(String input, ConfigurableEnvironment allProperties) {
        if (input == null) return null;
        StringBuffer output = new StringBuffer();
        Matcher matcher = decodePasswordPattern.matcher(input);
        while (matcher.find()) {
            String replacement = passwordDecoder.decodePassword(matcher.group(1), allProperties);
            matcher.appendReplacement(output, replacement);
        }
        matcher.appendTail(output);
        return output.toString();
    }
}
