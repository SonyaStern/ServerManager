package com.server.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jezhumble.javasysmon.JavaSysMon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public JavaSysMon javaSysMon() {
        return new JavaSysMon();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
