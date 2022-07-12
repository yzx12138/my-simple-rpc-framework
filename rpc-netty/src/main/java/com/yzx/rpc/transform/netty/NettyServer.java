package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.TransportServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;

/**
 * @author baozi
 * @Description: Netty实现客户端
 * @Date created on 2022/7/13
 */
public class NettyServer implements TransportServer {

    private ServerBootstrap serverBootstrap = null;
    private NioEventLoopGroup bossGroup = null;
    private NioEventLoopGroup workerGroup = null;
    private Channel channel = null;

    @Override
    public void startServer(int port) throws Exception {
        if (serverBootstrap == null) {
            serverBootstrap = createServerBootstrap();
        }
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        channel = channelFuture.channel();
    }

    private ServerBootstrap createServerBootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        if (bossGroup == null) {
            bossGroup = new NioEventLoopGroup();
        }
        if (workerGroup == null) {
            workerGroup = new NioEventLoopGroup();
        }
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RequestDecoder());
                        ch.pipeline().addLast(new ResponseEncoder());
                        ch.pipeline().addLast(new RequestInvocation());
                    }
                })
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return serverBootstrap;
    }

    @Override
    public void close() throws IOException {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (channel != null && channel.isActive()) {
            channel.close();
        }
    }
}
