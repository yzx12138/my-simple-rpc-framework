package com.yzx.rpc.server;

import com.yzx.rpc.spi.ServiceSupport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author baozi
 * @Description: 请求处理器注册
 * @Date created on 2022/7/13
 */
public class RequestHandlerRegistry {

    static Map<Integer, RequestHandler> handlerRegistry;

    static {
        // 通过SPI获取所有的请求处理器，并注册到handlerRegistry的map中
        handlerRegistry = new HashMap<>();
        Collection<RequestHandler> requestHandlers = ServiceSupport.loadAll(RequestHandler.class);
        for (RequestHandler requestHandler : requestHandlers) {
            handlerRegistry.put(requestHandler.getType(), requestHandler);
        }
    }

    public static RequestHandler getHandler(Integer type) {
        return handlerRegistry.get(type);
    }

}
