package com.yzx.rpc.hello;

/**
 * @author baozi
 * @Description: 远程调用服务API
 * @Date created on 2022/7/10
 */
public interface HelloService extends Cloneable{

    String hello(String name);
}
