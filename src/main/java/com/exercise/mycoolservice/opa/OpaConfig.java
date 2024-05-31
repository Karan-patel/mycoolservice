package com.exercise.mycoolservice.opa;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationProperties(prefix = "opa")
@Slf4j
@Data
public class OpaConfig {

    private String url;

    @Bean
    public WebClient opaWebClient() {
        log.info("Initializing webclient to consume api exposed by OPA.");
        return WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
