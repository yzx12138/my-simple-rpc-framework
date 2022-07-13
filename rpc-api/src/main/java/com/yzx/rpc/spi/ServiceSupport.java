package com.yzx.rpc.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author baozi
 * @Description: SPI类加载帮助类
 * @Date created on 2022/7/11
 */
public class ServiceSupport {

    private final static Map<String, Object> singletonServices = new HashMap<>();

    public static synchronized <S> S load(Class<S> clazz) {
        return StreamSupport.stream(ServiceLoader.load(clazz).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .findFirst()
                .orElseThrow(ServiceLoadException::new);
    }

    public static synchronized <S> Collection<S> loadAll(Class<S> clazz) {
        return StreamSupport
                .stream(ServiceLoader.load(clazz).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .collect(Collectors.toList());
    }

    private static <S>  S singletonFilter(S service) {
        if(service.getClass().isAnnotationPresent(Singleton.class)) {
            String className = service.getClass().getCanonicalName();
            Object singletonInstance = singletonServices.putIfAbsent(className, service);
            return singletonInstance == null ? service : (S) singletonInstance;
        } else {
            return service;
        }
    }
}
