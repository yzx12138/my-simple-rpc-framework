package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.Command;
import com.yzx.rpc.transform.InFlightRequests;
import com.yzx.rpc.transform.ResponseFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author baozi
 * @Description: Netty客户端处理器
 * @Date created on 2022/7/12
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<Command> {

    private final InFlightRequests inFlightRequests;

    public NettyClientHandler(InFlightRequests inFlightRequests) {
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {
        ResponseFuture future = inFlightRequests.remove(command.getHeader().getRequestId());
        if (future != null) {
            future.getCompletableFuture().complete(command);
        } else {
            // todo 打warn日志
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // todo 打warn日志
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
