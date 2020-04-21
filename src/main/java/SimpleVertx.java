import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

public class SimpleVertx extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route("/service/sayhello").handler(context->{
            context.response().end("hello,world");
        });

        vertx.createHttpServer().requestHandler(router).listen(8080, ar->{
            System.out.println("SimpleVert.x请求访问结果："+ar.succeeded());
        });
    }
}
