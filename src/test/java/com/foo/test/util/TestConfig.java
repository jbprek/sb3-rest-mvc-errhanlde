package com.foo.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

@TestConfiguration
public class TestConfig {
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    public TestConfig(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public TestUtil jsonReader() {
        return new TestUtil(objectMapper,resourceLoader);
    }
}
