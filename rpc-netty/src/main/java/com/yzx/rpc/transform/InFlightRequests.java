package com.yzx.rpc.transform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * @author baozi
 * @Description: 保存执行中的请求
 * @Date created on 2022/7/11
 */
public class InFlightRequests {

    /**
     * 存储执行中的请求
     */
    private Map<Long/*RequestId*/, ResponseFuture> map = new ConcurrentHashMap<>();

    /**
     * 限流
     */
    private Semaphore semaphore = new Semaphore(10);

    public void put(Long requestId, ResponseFuture responseFuture) throws InterruptedException {
        semaphore.acquire();
        map.put(requestId, responseFuture);
        // TODO 请求定时失效的逻辑
    }

    public void remove(Long requestId) {
        map.remove(requestId);
        semaphore.release();
    }

    public ResponseFuture get(Long requestId) {
        return map.get(requestId);
    }
}
