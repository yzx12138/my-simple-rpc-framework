package com.yzx.rpc;

import com.yzx.rpc.api.RpcAccessPoint;
import com.yzx.rpc.name.service.NameService;
import com.yzx.rpc.proxy.RpcProxySupport;
import com.yzx.rpc.server.ServiceProviderRegistry;
import com.yzx.rpc.spi.ServiceSupport;
import com.yzx.rpc.transform.TransformConstants;
import com.yzx.rpc.transform.Transport;
import com.yzx.rpc.transform.TransportClient;
import com.yzx.rpc.transform.TransportServer;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author baozi
 * @Description: Netty实现Rpc调用框架
 * @Date created on 2022/7/11
 */
public class NettyRpcAccessPoint implements RpcAccessPoint {

    private Map<URI, Transport> transportMap = new ConcurrentHashMap<>();
    private RpcProxySupport rpcProxy = new RpcProxySupport();

    private TransportClient transportClient = null;
    private TransportServer transportServer = null;
    private Collection<NameService> nameServices = null;
    private ServiceProviderRegistry serviceProviderRegistry = null;

    private final String host = "localhost";
    private final int port = 9999;
    private final URI uri = URI.create("rpc://" + host + ":" + port);

    @Override
    public <T> URI registeService(Class<T> serviceClazz, T service) {
        if (serviceProviderRegistry == null) {
            serviceProviderRegistry = ServiceSupport.load(ServiceProviderRegistry.class);
        }
        serviceProviderRegistry.addServiceProvider(serviceClazz, service);
        return uri;
    }

    @Override
    public <T> T getRemoteService(URI uri, Class<T> serviceClazz) {
        Transport transport = transportMap.computeIfAbsent(uri, this::createTransport);
        // 通过java的动态代理来实现【桩】
        return (T) rpcProxy.createProxy(serviceClazz, transport);
    }

    private <T> Transport createTransport(URI uri) {
        if (transportClient == null) {
            transportClient = ServiceSupport.load(TransportClient.class);
        }
        try {
            return transportClient.createTransport(
                    new InetSocketAddress(uri.getHost(), uri.getPort()),
                    TransformConstants.DEFAULT_CONN_TIME_OUT);
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized Closeable startServer() throws Exception {
        if (transportServer == null) {
            transportServer = ServiceSupport.load(TransportServer.class);
            transportServer.startServer(port);
        }

        return () -> {
            if (transportServer != null) {
                transportServer.close();
            }
        };
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
        if (transportClient != null) {
            transportClient.close();
        }
        if (transportServer != null) {
            transportServer.close();
        }
    }
}
