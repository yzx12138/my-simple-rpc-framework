package com.yzx.rpc.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author baozi
 * @Description: SPI类加载帮助类
 * @Date created on 2022/7/11
 */
public class ServiceSupport {

    public static synchronized <S> S load(Class<S> clazz) {
        return StreamSupport.stream(ServiceLoader.load(clazz).spliterator(), false)
                .findFirst()
                .orElseThrow(ServiceLoadException::new);
    }

    public static synchronized <S> Collection<S> loadAll(Class<S> clazz) {
        return StreamSupport
                .stream(ServiceLoader.load(clazz).spliterator(), false)
                .collect(Collectors.toList());
    }
}
