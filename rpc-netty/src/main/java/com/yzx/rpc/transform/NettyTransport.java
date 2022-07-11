package com.yzx.rpc.transform;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.CompletableFuture;

/**
 * @author baozi
 * @Description: Netty实现消息发送
 * @Date created on 2022/7/11
 */
public class NettyTransport implements Transport {

    /**
     * 存储已发送执行中的请求
     */
    private InFlightRequests inFlightRequests;
    private Channel channel;

    public NettyTransport(InFlightRequests inFlightRequests, Channel channel) {
        this.inFlightRequests = inFlightRequests;
        this.channel = channel;
    }

    @Override
    public CompletableFuture<Command> send(Command request) {
        CompletableFuture<Command> completableFuture = new CompletableFuture<>();
        try {
            ResponseFuture responseFuture = new ResponseFuture(request.getHeader().getRequestId(), completableFuture);
            inFlightRequests.put(request.getHeader().getRequestId(), responseFuture);
            channel.writeAndFlush(request).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    completableFuture.completeExceptionally(channelFuture.cause());
                    channel.close();
                }
            });
        } catch (Throwable e) {
            inFlightRequests.remove(request.getHeader().getRequestId());
            completableFuture.completeExceptionally(e.getCause());
        }
        return completableFuture;
    }
}
