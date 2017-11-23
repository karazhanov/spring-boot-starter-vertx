package com.github.karazhanov.configuration.vertx;

import com.github.karazhanov.configuration.VertXProperties;
import com.github.karazhanov.configuration.vertx.controllers.ResponseFailSender;
import com.github.karazhanov.configuration.vertx.controllers.ResponseSender;
import com.github.karazhanov.configuration.vertx.controllers.VertxController;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@Slf4j
public class DefaultServerVerticle extends AbstractVerticle {


    @Autowired
    private BodyHandler bodyHandler;
    @Autowired
    private CookieHandler cookieHandler;
    @Autowired
    private ErrorHandler errorHandler;
    @Autowired
    private StaticHandler staticHandler;
    @Autowired
    private TimeoutHandler timeoutHandler;
    @Autowired
    private VertXProperties properties;
    @Autowired
    private ResponseSender response;
    @Autowired
    private ResponseFailSender responseFail;
    @Autowired
    private Collection<VertxController> vertxControllers;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        log.info("Starting server with config " + properties);
        HttpServer httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.exceptionHandler(e -> log.error("Unhandled exception while routing", e));

        router.route().handler(timeoutHandler);
        router.route().handler(this::handleStatic);
        router.route().handler(bodyHandler);
        router.route().handler(cookieHandler);

        startControllers(router);

        router.route().handler(rc -> {
            if (!rc.request().response().ended()) {
                rc.fail(HttpResponseStatus.NOT_FOUND.code());
            }
        }).failureHandler(errorHandler);

        if (properties.isShowDebugInfo()) {
            printRoutes(router);
        }

        int serverPort = properties.getPort();
        httpServer
                .requestHandler(router::accept)
                .listen(serverPort, event -> {
                    if (event.succeeded()) {
                        log.info("Server started. Listening port: " + serverPort);
                        startFuture.complete();
                    } else {
                        log.error("Fail to start server. Listening port: " + serverPort, event.cause());
                        startFuture.fail(event.cause());
                    }
                });
    }



    private void handleStatic(RoutingContext rc) {
        String path = rc.request().path();
        boolean isStatic = properties.getStaticFileExtensions().stream().anyMatch(path::endsWith);
        if (isStatic) {
            staticHandler.handle(rc);
        } else {
            rc.next();
        }
    }

    private void startControllers(Router router) {
        if (vertxControllers != null) {
            vertxControllers.forEach(vertxController -> vertxController.addToRouting(router, response, responseFail));
        }
    }

    private void printRoutes(Router router) {
        List<Route> routes = router.getRoutes();
        log.info("Utils Vert.X handlers");
        for (Route route : routes) {
            if (route.getPath() == null) {
                log.info("\t" + foundHandlerClass(route));
            }
        }
        log.info("REST Vert.X handlers");
        for (Route route : routes) {
            if (route.getPath() != null) {
                log.info("\t" + route.getPath() + " -> " + foundHandlerClass(route));
            }
        }
    }

    private static final String HANDLER = "handler:";
    private static final String COMMERCIAL_AT = "@";

    private String foundHandlerClass(Route route) {
        String s = route.toString();
        int startIndex = s.indexOf(HANDLER) + HANDLER.length();
        int endIndex = s.indexOf(COMMERCIAL_AT);
        return s.substring(startIndex, endIndex);
    }
}
