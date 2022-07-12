package com.yzx.rpc.server;

import com.yzx.rpc.transform.Command;

/**
 * @author baozi
 * @Description: 请求处理器
 * @Date created on 2022/7/13
 */
public interface RequestHandler {

    Integer getType();

    Command handle(Command request);
}
