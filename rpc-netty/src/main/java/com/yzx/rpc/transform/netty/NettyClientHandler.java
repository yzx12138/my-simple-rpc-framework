package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.Command;
import com.yzx.rpc.transform.InFlightRequests;
import com.yzx.rpc.transform.ResponseFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author baozi
 * @Description: Netty客户端处理器
 * @Date created on 2022/7/12
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<Command> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

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
            logger.warn("[NettyClientHandler - channelRead0] future has been removed");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("[NettyClientHandler - exceptionCaught] Exception: ", cause);
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
