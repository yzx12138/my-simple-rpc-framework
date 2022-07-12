package com.yzx.rpc.server;

import com.yzx.rpc.api.RpcAccessPoint;
import com.yzx.rpc.hello.HelloService;
import com.yzx.rpc.name.service.NameService;
import com.yzx.rpc.spi.ServiceSupport;

import java.io.File;
import java.net.URI;

/**
 * @author baozi
 * @Description: 客户端实现
 * @Date created on 2022/7/10
 */
public class Server {

    public static void main(String[] args) throws Exception {
        String serviceName = HelloService.class.getCanonicalName();
        HelloService helloService = new HelloServiceImpl();
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDirFile, "my_simple_rpc_name_service.data");
        URI nameServiceUri = file.toURI();

        try (RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class)) {
            // 启动服务端
            rpcAccessPoint.startServer();
            // 注册服务到NameServer
            URI uri = rpcAccessPoint.registeService(HelloService.class, helloService);
            if (uri == null) {
                throw new RuntimeException("register service fail");
            }
            NameService nameService = rpcAccessPoint.getNameService(nameServiceUri);
            nameService.registerService(serviceName, uri);

            System.in.read();
            System.out.println("Bye ~");
        }
    }
}
