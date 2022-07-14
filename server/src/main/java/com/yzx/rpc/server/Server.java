package com.yzx.rpc.server;

import com.yzx.rpc.api.RpcAccessPoint;
import com.yzx.rpc.hello.HelloService;
import com.yzx.rpc.name.service.NameService;
import com.yzx.rpc.spi.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * @author baozi
 * @Description: 客户端实现
 * @Date created on 2022/7/10
 */
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        String serviceName = HelloService.class.getCanonicalName();
        HelloService helloService = new HelloServiceImpl();
        URI nameServiceUri = getJDBCNameServiceURI();
        //URI nameServiceUri = getFileNameServiceURI();

        try (RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class)) {
            // 启动服务端
            rpcAccessPoint.startServer();
            logger.info("server started");
            // 注册服务到NameServer
            URI uri = rpcAccessPoint.registeService(HelloService.class, helloService);
            if (uri == null) {
                throw new RuntimeException("register service fail");
            }
            NameService nameService = rpcAccessPoint.getNameService(nameServiceUri);
            nameService.registerService(serviceName, uri);
            logger.info("server register ok");


            System.in.read();
            logger.info("Bye ~");
        }
    }

    private static URI getJDBCNameServiceURI() throws IOException {
        String uri = "jdbc:mysql://localhost:3306/test";
        return URI.create(uri);
    }

    private static URI getFileNameServiceURI() throws IOException {
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDirFile, "my_simple_rpc_name_service.data");
        return file.toURI();
    }
}
