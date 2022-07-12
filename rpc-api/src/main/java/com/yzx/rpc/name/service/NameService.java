package com.yzx.rpc.name.service;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
 * @author baozi
 * @Description: 注册中心
 * @Date created on 2022/7/10
 */
public interface NameService {

    /**
     * 注册服务
     * @param serviceName
     * @param uri
     * @throws IOException
     */
    void registerService(String serviceName, URI uri) throws IOException;

    /**
     * 查询服务地址
     * @param serviceName
     * @return
     * @throws IOException
     */
    URI lookupService(String serviceName) throws IOException;

    /**
     * 支持的协议
     * @return
     */
    Collection<String> supportedSchemes();

    /**
     * 连接到NameServer注册中心
     * @param nameServiceUri
     */
    void connect(URI nameServiceUri);
}
