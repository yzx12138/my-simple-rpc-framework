package com.yzx.rpc.server;

/**
 * @author baozi
 * @Description: 服务端服务提供注册
 * @Date created on 2022/7/13
 */
public interface ServiceProviderRegistry {

    <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider);
}
