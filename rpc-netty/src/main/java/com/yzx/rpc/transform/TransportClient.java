package com.yzx.rpc.transform;

import java.io.Closeable;
import java.net.SocketAddress;

/**
 * @author baozi
 * @Description: 客户端
 * @Date created on 2022/7/11
 */
public interface TransportClient extends Closeable {

    /**
     * 创建传输
     * @param address
     * @param connectionTimeout
     * @return
     */
    Transport createTransport(SocketAddress address, long connectionTimeout);
}
