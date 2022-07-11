package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.Transport;
import com.yzx.rpc.transform.TransportClient;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * @author baozi
 * @Description: Netty客户端
 * @Date created on 2022/7/11
 */
public class NettyClient implements TransportClient {

    @Override
    public Transport createTransport(SocketAddress address, long connectionTimeout) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
