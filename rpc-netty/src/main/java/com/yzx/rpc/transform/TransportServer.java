package com.yzx.rpc.transform;

import java.io.Closeable;

/**
 * @author baozi
 * @Description: 服务端
 * @Date created on 2022/7/13
 */
public interface TransportServer extends Closeable {

    void startServer(int port) throws Exception;
}
