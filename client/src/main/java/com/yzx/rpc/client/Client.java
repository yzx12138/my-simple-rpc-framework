package com.yzx.rpc.client;

import com.yzx.rpc.api.RpcAccessPoint;
import com.yzx.rpc.hello.HelloService;
import com.yzx.rpc.spi.ServiceSupport;

import java.net.URI;

/**
 * @author baozi
 * @Description: 客户端
 * @Date created on 2022/7/10
 */
public class Client {

    public static void main(String[] args) {
        RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);
        URI uri = URI.create("rpc://localhost:9999");
        HelloService helloService = rpcAccessPoint.getRemoteService(uri, HelloService.class);
        String name = helloService.hello("name");
        System.out.println(name);
    }
}
