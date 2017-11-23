package com.github.karazhanov.configuration;

import com.github.karazhanov.configuration.vertx.DefaultServerVerticle;
import com.github.karazhanov.configuration.vertx.controllers.ResponseFailSender;
import com.github.karazhanov.configuration.vertx.controllers.ResponseSender;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.handler.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author karazhanov on 06.11.17.
 */
@Slf4j
@Configuration
@ConditionalOnClass(EnableVertX.class)
@EnableConfigurationProperties(VertXProperties.class)
public class VertXAutoConfiguration {

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    @ConditionalOnMissingBean(TimeoutHandler.class)
    public TimeoutHandler timeoutHandler(VertXProperties properties) {
        return TimeoutHandler.create(properties.getRequestTimeout());
    }

    @Bean
    @ConditionalOnMissingBean(BodyHandler.class)
    public BodyHandler bodyHandler() {
        return BodyHandler.create();
    }

    @Bean
    @ConditionalOnMissingBean(CookieHandler.class)
    public CookieHandler cookieHandler() {
        return CookieHandler.create();
    }

    @Bean
    @ConditionalOnMissingBean(ErrorHandler.class)
    public ErrorHandler errorHandler() {
        return ErrorHandler.create();
    }

    @Bean
    @ConditionalOnMissingBean(StaticHandler.class)
    public StaticHandler staticHandler() {
        return StaticHandler.create();
    }


    @Bean
    @ConditionalOnMissingBean(AbstractVerticle.class)
    public AbstractVerticle verticle() {
        return new DefaultServerVerticle();
    }


    @Bean
    @ConditionalOnMissingBean(ResponseSender.class)
    public ResponseSender responseSender() {
        return new ResponseSender.DefaultResponseSender();
    }

    @Bean
    @ConditionalOnMissingBean(ResponseFailSender.class)
    public ResponseFailSender responseFailSender() {
        return new ResponseFailSender.DefaultResponseSender();
    }

    @Configuration
    public static class Registrator implements ApplicationContextAware {

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            Vertx vertx = applicationContext.getBean(Vertx.class);
            assert vertx != null;
            AbstractVerticle defaultServerVerticle= applicationContext.getBean(AbstractVerticle.class);
            assert defaultServerVerticle != null;
            vertx.deployVerticle(defaultServerVerticle, e ->  {
                if(e.failed()){
                    log.error("Fail to deploy server verticle. Shutdown vertx.", e);
                    vertx.close();
                }
            });
        }

    }
}
