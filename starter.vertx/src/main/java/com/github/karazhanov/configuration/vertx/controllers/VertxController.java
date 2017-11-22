package com.github.karazhanov.configuration.vertx.controllers;

import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;
import rx.Observable;

/**
 * @author karazhanov on 18.10.17.
 */
@Component
public abstract class VertxController<T> implements Handler<RoutingContext> {

    private ResponseSender response;
    private ResponseFailSender responseFail;

    private METHOD_TYPE methodType;
    private String path;

    public VertxController(METHOD_TYPE methodType, String path) {
        this.methodType = methodType;
        this.path = path;
    }

    public void addToRouting(Router router, ResponseSender response, ResponseFailSender responseFail) {
        this.response = response;
        this.responseFail = responseFail;
        switch (methodType) {
            case GET:
                router.get(path).handler(this);
                break;
            case POST:
                router.post(path).handler(this);
                break;
            case PUT:
                router.put(path).handler(this);
                break;
            case DELETE:
                router.delete(path).handler(this);
                break;
        }
    }

    @Override
    public final void handle(RoutingContext rc) {
        Observable
                .just(execute(rc))
                .subscribe(
                        o -> response.sendResult(rc, o),
                        throwable -> responseFail.sendError(rc, throwable)
                );
    }

    protected abstract T execute(RoutingContext routingContext);
}
