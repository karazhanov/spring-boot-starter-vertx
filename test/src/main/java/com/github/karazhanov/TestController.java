package com.github.karazhanov;

import com.github.karazhanov.vertx.controllers.METHOD_TYPE;
import com.github.karazhanov.vertx.controllers.VertxController;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class TestController extends VertxController {
    public TestController() {
        super(METHOD_TYPE.GET, "/user/:name");
    }

    @Override
    protected Object execute(RoutingContext routingContext) {
        String name = routingContext.request().getParam("name");
        return "HELLO " + name;
    }
}
