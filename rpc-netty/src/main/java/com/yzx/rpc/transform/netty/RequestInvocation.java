package com.yzx.rpc.transform.netty;

import com.yzx.rpc.server.RequestHandler;
import com.yzx.rpc.server.RequestHandlerRegistry;
import com.yzx.rpc.transform.Command;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author baozi
 * @Description: 请求处理handler
 * @Date created on 2022/7/13
 */
@ChannelHandler.Sharable
public class RequestInvocation extends SimpleChannelInboundHandler<Command> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command request) throws Exception {
        // 根据协议类型来选择不同的handler处理逻辑
        RequestHandler handler = RequestHandlerRegistry.getHandler(request.getHeader().getType());
        Command response;
        if (handler == null) {
            response = Command.buildFailCommand(request, "msg type handler not exist");
            System.out.printf("no hanlder for msg type: %d", request.getHeader().getType());
        } else {
            response = handler.handle(request);
        }
        ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                ctx.channel().close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }
}
