package com.yzx.rpc.transform;

import java.util.concurrent.CompletableFuture;

/**
 * @author baozi
 * @Description: 调用发送接口
 * @Date created on 2022/7/11
 */
public interface Transport {

    /**
     * 发送请求
     * @param request
     * @return
     */
    CompletableFuture<Command> send(Command request);
}
