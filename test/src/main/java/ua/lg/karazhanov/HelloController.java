package ua.lg.karazhanov;

import com.github.karazhanov.annotations.VertXRestController;
import com.github.karazhanov.annotations.params.Path;
import io.vertx.ext.web.RoutingContext;
import rx.Observable;
import com.github.karazhanov.annotations.methods.GET;
import com.github.karazhanov.annotations.params.Query;

@VertXRestController
public class HelloController {

    @GET
    public String index() {
        return "HELLO";
    }

    @GET("hello/:s")
    public String test1(@Path String s, @Query Object o) {
        return s;
    }

    @GET(":id")
    public Object observableID(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        Observable<?> o = Observable.just(id);
        return "HELLO " + id;
    }

}