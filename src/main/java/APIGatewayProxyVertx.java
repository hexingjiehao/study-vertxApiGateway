import com.sun.istack.internal.NotNull;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.Map;

public class APIGatewayProxyVertx extends AbstractVerticle {

    private final Map<String, WebClient> serviceMap;

    public APIGatewayProxyVertx(Map<String, WebClient> serviceMap){
        this.serviceMap = serviceMap;
    }

    /**
     * 一般情况下：网关的api接口比实际的api接口要清爽。比如
     * compute: service/add
     * agateway: /add
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        for (String serviceName : serviceMap.keySet()) {
            router.route(String.format("/%s", serviceName)).handler(context -> {
                //1.验证请求头

                //2.分发请求
                doDispatch(context, serviceMap.get(serviceName));
            });
        }

        vertx.createHttpServer().requestHandler(router).listen(8081, ar->{
            System.out.println("APIGatewayProxyVertx请求访问结果："+ar.succeeded());
        });
    }

    private void doDispatch(RoutingContext context, @NotNull WebClient client) {
        final HttpRequest<Buffer> toReq = client.request(context.request().method(), "/service"+context.request().uri())
                .putHeaders(context.request().headers());

        final Handler<AsyncResult<HttpResponse<Buffer>>> responseHandler = ar -> {
            HttpResponse<Buffer> response = ar.result();
            HttpServerResponse toRsp = context.response();

            if (ar.succeeded()) {
                toRsp.setStatusCode(response.statusCode());
                toRsp.headers().setAll(response.headers());

                if (response.body() == null) {
                    toRsp.end();
                } else {
                    toRsp.end(response.body());
                }
            } else {
                toRsp.setStatusCode(502).end(ar.cause().getMessage());
            }
        };

        if (context.getBody() == null) {
            toReq.send(responseHandler);
        } else {
            toReq.sendBuffer(context.getBody(), responseHandler);
        }
    }

}
