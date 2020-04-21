import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class APIGatewayVertxTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context){
        vertx = Vertx.vertx();
        vertx.deployVerticle(SimpleVertx.class.getName(), context.asyncAssertSuccess(response ->
                vertx.deployVerticle(APIGatewayControllerVertx.class.getName(),context.asyncAssertSuccess(res->{
                    System.out.println("APIGatewayControllerVertx和APIGatewayProxyVertx和SimpleVertx部署到容器vertx完成:"+response);
                }))
        ));
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess(response ->
                System.out.println("容器vertx拆卸完成，里面的APIGatewayControllerVertx和APIGatewayProxyVertx和SimpleVertx同时也被拆卸完成："+response)
        ));
    }

    @Test
    public void testApiGateway(TestContext context){
        final Async async = context.async();

//        vertx.createHttpClient()
//                .getNow(8080, "localhost", "/service/sayhello", response -> {
//                    response.result().handler(body -> {
//                        System.out.println(body.toString());
//                        async.complete();
//                    });
//                });

        vertx.createHttpClient()
                .getNow(8081, "localhost", "/sayhello", response -> {
                    response.result().handler(body -> {
                        System.out.println(body.toString());
                        async.complete();
                    });
                });
    }

}
