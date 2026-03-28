package com.education.education.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.configure().directory(".").ignoreIfMissing().load();

        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        Map<String, Object> dotenvMap =
                dotenv.entries().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvMap));
    }
}
