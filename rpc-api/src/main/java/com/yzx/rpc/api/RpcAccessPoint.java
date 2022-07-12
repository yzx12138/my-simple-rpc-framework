package com.yzx.rpc.api;

import com.yzx.rpc.name.service.NameService;

import java.io.Closeable;
import java.net.URI;

/**
 * @author baozi
 * @Description: Rpc框架提供的api
 * @Date created on 2022/7/10
 */
public interface RpcAccessPoint extends Closeable {

    /**
     * 注册服务
     * @param serviceClazz
     * @param service
     * @param <T>
     * @return
     */
    <T> URI registeService(Class<T> serviceClazz, T service);

    /**
     * 客户端获取远程调用服务的引用
     * @param uri
     * @param serviceClazz
     * @param <T>
     * @return
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClazz);

    /**
     * 启动服务
     * @return
     * @throws Exception
     */
    Closeable startServer() throws Exception;

    /**
     * 获取注册中心实例
     * @param nameServiceUri
     * @return
     */
    NameService getNameService(URI nameServiceUri);
}
