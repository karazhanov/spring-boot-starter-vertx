# Spring boot starter for Vert.X

In **build.gradle** add 

    repositories {
        maven { url 'https://jitpack.io' }
    }
    
    dependencies {
        compile ("com.github.karazhanov:spring-boot-starter-vertx:VERSION") {
            exclude module: 'test'
        }
    }
Last **VERSION="0.0.7"**

In **application.properties** you can add 

    vertx.port=8080
    vertx.requestTimeout=2000
    vertx.showDebugInfo=true
    vertx.staticFileExtensions=.html,.js

For enable this for SpringBoot just annotate **@EnableVertX**

    @SpringBootApplication
    @EnableVertX
    public class MainAppTest {
        public static void main(String[] args) {
            SpringApplication.run(MainAppTest.class, args);
        }
    }
    
## Manual using Vert.X controllers
    @Component
    public class TestController extends VertxController {
        public TestController() {
            super(METHOD_TYPE.GET, "/:name");
        }
    
        @Override
        protected Object execute(RoutingContext routingContext) {
            String name = routingContext.request().getParam("name");
            return "HELLO " + name;
        }
    }
  
## Automate generating controllers _(work in progress)_

    @VertXRestController("/")
    public class HelloController {
    
        @GET
        public String index() {
            return "HELLO";
        }
        
        @GET(":s")
        public String indexId(@Path String s) {
            return "HELLO " + s;
        }
    }

### @VertXRestController
### @GET
### @POST
### @PUT
### @DELETE
