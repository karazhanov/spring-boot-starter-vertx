package com.github.karazhanov;

import com.github.karazhanov.vertx.controllers.METHOD_TYPE;
import com.github.karazhanov.vertx.controllers.VertxController;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class TestController extends VertxController<String> {
    public TestController() {
        super(METHOD_TYPE.GET, "/user/:name");
    }

    @Override
    protected String execute(RoutingContext routingContext) {
        String name = routingContext.request().getParam("name");
        return "HELLO " + name;
    }
}
