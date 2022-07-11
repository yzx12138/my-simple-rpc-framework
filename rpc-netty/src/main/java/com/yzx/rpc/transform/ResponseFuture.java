package com.yzx.rpc.transform;

import java.util.concurrent.CompletableFuture;

/**
 * @author baozi
 * @Description: 返回请求
 * @Date created on 2022/7/11
 */
public class ResponseFuture {

    private final long requestId;
    private final CompletableFuture completableFuture;
    private final long timeStamp;

    public ResponseFuture(long requestId, CompletableFuture completableFuture) {
        this.requestId = requestId;
        this.completableFuture = completableFuture;
        this.timeStamp = System.nanoTime();
    }

    public long getRequestId() {
        return requestId;
    }

    public CompletableFuture getCompletableFuture() {
        return completableFuture;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
