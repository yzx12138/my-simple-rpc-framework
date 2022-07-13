package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.InFlightRequests;
import com.yzx.rpc.transform.NettyTransport;
import com.yzx.rpc.transform.Transport;
import com.yzx.rpc.transform.TransportClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

/**
 * @author baozi
 * @Description: Netty客户端
 * @Date created on 2022/7/11
 */
public class NettyClient implements TransportClient {

    private InFlightRequests inFlightRequests = new InFlightRequests();

    private Bootstrap bootstrap;
    private List<Channel> channels = new LinkedList<>();
    private EventLoopGroup ioEventGroup;

    @Override
    public Transport createTransport(SocketAddress address, long connectionTimeout)
            throws InterruptedException, TimeoutException {
        if (address == null) {
            throw new IllegalArgumentException("address should not be null");
        }
        if (bootstrap == null) {
            bootstrap = createBootstrap();
        }
        ChannelFuture future = bootstrap.connect(address);
        if (!future.await(connectionTimeout)) {
            throw new TimeoutException("connect host time out");
        }
        Channel channel = future.channel();
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException();
        }
        channels.add(channel);
        return new NettyTransport(inFlightRequests, channel);
    }

    private Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        if (ioEventGroup == null) {
            ioEventGroup = new NioEventLoopGroup();
        }
        bootstrap.group(ioEventGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ResponseDecoder());
                        socketChannel.pipeline().addLast(new RequestEncoder());
                        socketChannel.pipeline().addLast(new NettyClientHandler(inFlightRequests));
                    }
                })
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        return bootstrap;
    }

    @Override
    public void close() throws IOException {
        for (Channel channel : channels) {
            if (channel != null && channel.isActive()) {
                channel.close();
            }
        }
        if (ioEventGroup != null) {
            ioEventGroup.shutdownGracefully();
        }
        inFlightRequests.close();
    }
}
