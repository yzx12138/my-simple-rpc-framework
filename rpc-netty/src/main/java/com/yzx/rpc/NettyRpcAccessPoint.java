package com.yzx.rpc;

import com.yzx.rpc.api.RpcAccessPoint;
import com.yzx.rpc.proxy.RpcProxySupport;
import com.yzx.rpc.transform.Transport;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baozi
 * @Description: Netty实现Rpc调用框架
 * @Date created on 2022/7/11
 */
public class NettyRpcAccessPoint implements RpcAccessPoint {

    //public static void main(String[] args) {
    //    RpcProxySupport rpcProxy = new RpcProxySupport();
    //    HelloService proxy = (HelloService) rpcProxy.createProxy(HelloService.class, new NettyTransport(null, null));
    //    System.out.println(proxy.hello("aaa"));
    //}

    private Map<URI, Transport> transportMap = new ConcurrentHashMap<>();
    private RpcProxySupport rpcProxy = new RpcProxySupport();

    @Override
    public <T> URI registeService(Class<T> serviceClazz, T service) {
        return null;
    }

    @Override
    public <T> T getRemoteService(URI uri, Class<T> serviceClazz) {
        Transport transport = transportMap.computeIfAbsent(uri, this::createTransport);
        // 通过java的动态代理来实现【桩】
        return (T) rpcProxy.createProxy(serviceClazz, transport);
    }

    private <T> Transport createTransport(URI uri) {
        return null;
    }

    @Override
    public Cloneable startServer() throws Exception {
        return null;
    }
}
