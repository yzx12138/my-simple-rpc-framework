package com.yzx.rpc.client;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author baozi
 * @Description: 请求id获取
 * @Date created on 2022/7/12
 */
public class RequestIdSupport {
    private final static LongAdder adder = new LongAdder();

    public static long next() {
        adder.increment();
        return adder.longValue();
    }
}
