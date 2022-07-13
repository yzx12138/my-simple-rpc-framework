package com.yzx.rpc.transform;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author baozi
 * @Description: 保存执行中的请求
 * @Date created on 2022/7/11
 */
public class InFlightRequests implements Closeable {

    /**
     * 存储执行中的请求
     */
    private Map<Long/*RequestId*/, ResponseFuture> map = new ConcurrentHashMap<>();

    /**
     * 限流
     */
    private Semaphore semaphore = new Semaphore(10);

    private final static long TIME_OUT = 10L;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;

    public InFlightRequests() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeOutFutures, TIME_OUT, TIME_OUT, TimeUnit.SECONDS);
    }

    public void put(Long requestId, ResponseFuture responseFuture) throws InterruptedException {
        semaphore.acquire();
        map.put(requestId, responseFuture);
    }

    public ResponseFuture remove(Long requestId) {
        ResponseFuture future = map.remove(requestId);
        semaphore.release();
        return future;
    }

    public ResponseFuture get(Long requestId) {
        return map.get(requestId);
    }

    private void removeTimeOutFutures() {
        map.entrySet().removeIf(entry -> {
           if (System.nanoTime() - entry.getValue().getTimeStamp() > TIME_OUT * 1000_000_000) {
               semaphore.release();
               return true;
           } else {
               return false;
           }
        });
    }

    @Override
    public void close() throws IOException {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
