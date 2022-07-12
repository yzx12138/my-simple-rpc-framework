package com.yzx.rpc.client;

import com.yzx.rpc.api.RpcAccessPoint;
import com.yzx.rpc.hello.HelloService;
import com.yzx.rpc.name.service.NameService;
import com.yzx.rpc.spi.ServiceSupport;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * @author baozi
 * @Description: 客户端
 * @Date created on 2022/7/10
 */
public class Client {

    public static void main(String[] args) throws IOException {
        String serviceName = HelloService.class.getCanonicalName();
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDirFile, "my_simple_rpc_name_service.data");

        try (RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class)) {
            NameService nameService = rpcAccessPoint.getNameService(file.toURI());
            if (nameService == null) {
                System.out.println("name service don't exist");
                return;
            }
            URI uri = nameService.lookupService(serviceName);
            if (uri == null) {
                System.out.println("service uri don't exist");
                return;
            }
            HelloService helloService = rpcAccessPoint.getRemoteService(uri, HelloService.class);
            String name = helloService.hello("name");
            System.out.println(name);
        }
    }
}
