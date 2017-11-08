# Spring boot starter for Vert.X

For enable this for SpringBoot just annotate **@EnableVertX**

    @SpringBootApplication
    @ComponentScan
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
