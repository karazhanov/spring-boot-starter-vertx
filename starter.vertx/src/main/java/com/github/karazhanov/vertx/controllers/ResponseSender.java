package com.github.karazhanov.vertx.controllers;

import com.github.karazhanov.vertx.utils.ContentTypes;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

/**
 * @author karazhanov on 07.11.17.
 */
public abstract class ResponseSender {

    public abstract void sendResult(RoutingContext rc, Object result);

    public static class DefaultResponseSender extends ResponseSender {

        @Override
        public void sendResult(RoutingContext rc, Object result) {
            HttpServerResponse response = rc.response();
            response.putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON_UTF_8);
            if (result instanceof Buffer) {
                response.end((Buffer) result);
            } else {
                response.end(Json.encode(result));
            }

        }
    }
}
