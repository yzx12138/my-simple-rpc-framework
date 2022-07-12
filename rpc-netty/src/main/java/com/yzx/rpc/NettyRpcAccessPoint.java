package com.yzx.rpc;

import com.yzx.rpc.api.RpcAccessPoint;
import com.yzx.rpc.hello.HelloService;
import com.yzx.rpc.name.service.NameService;
import com.yzx.rpc.proxy.RpcProxySupport;
import com.yzx.rpc.serialize.serializer.Serializer;
import com.yzx.rpc.spi.ServiceSupport;
import com.yzx.rpc.transform.NettyTransport;
import com.yzx.rpc.transform.TransformConstants;
import com.yzx.rpc.transform.Transport;
import com.yzx.rpc.transform.TransportClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author baozi
 * @Description: Netty实现Rpc调用框架
 * @Date created on 2022/7/11
 */
public class NettyRpcAccessPoint implements RpcAccessPoint {

    public static void main(String[] args) {
        Serializer serializer = ServiceSupport.load(Serializer.class);
        System.out.println(serializer.type());
        RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);
        System.out.println(rpcAccessPoint);
    }

    private Map<URI, Transport> transportMap = new ConcurrentHashMap<>();
    private RpcProxySupport rpcProxy = new RpcProxySupport();

    private TransportClient transportClient = ServiceSupport.load(TransportClient.class);
    private Collection<NameService> nameServices = null;

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
        try {
            return transportClient.createTransport(
                    new InetSocketAddress(uri.getHost(), uri.getPort()),
                    TransformConstants.DEFAULT_CONN_TIME_OUT);
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Cloneable startServer() throws Exception {
        return null;
    }

    @Override
    public NameService getNameService(URI nameServiceUri) {
        if (nameServices == null) {
            nameServices = ServiceSupport.loadAll(NameService.class);
        }
        for (NameService nameService : nameServices) {
            // 找到对应协议的NameService，连接并返回
            if (nameService.supportedSchemes().contains(nameServiceUri.getScheme())) {
                nameService.connect(nameServiceUri);
                return nameService;
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
