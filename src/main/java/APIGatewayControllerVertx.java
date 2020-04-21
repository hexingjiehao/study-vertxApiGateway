import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.util.HashMap;

public class APIGatewayControllerVertx extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        //1.获取配置文件中的被代理的具体的API接口，这里暂时写死
        HashMap map = new HashMap<String, WebClient>();
        map.put("sayhello", WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost("localhost")
                .setDefaultPort(8080))
        );
        vertx.deployVerticle(new APIGatewayProxyVertx(map), new DeploymentOptions(), ar->{
            System.out.println("APIGatewayControllerVertx的部署结果："+ar.result());
        });
    }

}
