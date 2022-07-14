package com.yzx.rpc.client;

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
 * @Description: 客户端
 * @Date created on 2022/7/10
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        String serviceName = HelloService.class.getCanonicalName();
        URI nameServiceUri = getJDBCNameServiceURI();
        //URI nameServiceUri = getFileNameServiceURI();

        try (RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class)) {
            NameService nameService = rpcAccessPoint.getNameService(nameServiceUri);
            if (nameService == null) {
                logger.warn("name service don't exist");
                return;
            }
            URI uri = nameService.lookupService(serviceName);
            if (uri == null) {
                logger.warn("service uri don't exist");
                return;
            }
            HelloService helloService = rpcAccessPoint.getRemoteService(uri, HelloService.class);
            String name = helloService.hello("name");
            System.out.println(name);
            logger.info("client end~");
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
