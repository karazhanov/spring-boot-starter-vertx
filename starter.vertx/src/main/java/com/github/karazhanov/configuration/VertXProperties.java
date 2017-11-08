package com.github.karazhanov.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author karazhanov on 06.11.17.
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "vertx")
public class VertXProperties {
    private int port = 80;
    private long requestTimeout = 1000;
}
