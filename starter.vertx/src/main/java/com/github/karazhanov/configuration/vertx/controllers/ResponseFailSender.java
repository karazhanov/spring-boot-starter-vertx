package com.github.karazhanov.configuration.vertx.controllers;

import io.vertx.ext.web.RoutingContext;

/**
 * @author karazhanov on 07.11.17.
 */
public abstract class ResponseFailSender {

    public abstract void sendError(RoutingContext rc, Throwable throwable);

    public static class DefaultResponseSender extends ResponseFailSender {
        public void sendError(RoutingContext rc, Throwable throwable) {
            rc.fail(throwable);
        }
    }
}
