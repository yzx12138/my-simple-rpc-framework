package com.yzx.rpc.server;

import com.yzx.rpc.hello.HelloService;

/**
 * @author baozi
 * @Description: HelloService实现类，远程服务提供方
 * @Date created on 2022/7/13
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "hello:" + name;
    }
}
