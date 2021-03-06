package com.yzx.rpc.proxy;

import com.yzx.rpc.client.RequestIdSupport;
import com.yzx.rpc.client.ServiceTypes;
import com.yzx.rpc.serialize.SerializeSupport;
import com.yzx.rpc.transform.Command;
import com.yzx.rpc.transform.Header;
import com.yzx.rpc.transform.ResponseHeader;
import com.yzx.rpc.transform.RpcRequest;
import com.yzx.rpc.transform.Transport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author baozi
 * @Description: Rpc代理类生成
 * @Date created on 2022/7/11
 */
public class RpcProxySupport implements InvocationHandler {

    Map<Class, Transport> transportMap = new ConcurrentHashMap<>();

    public Object createProxy(Class serviceClazz, Transport transport) {
        Class[] classes = Arrays.copyOf(serviceClazz.getInterfaces(), serviceClazz.getInterfaces().length + 1);
        classes[classes.length-1] = serviceClazz;
        Object proxy = Proxy.newProxyInstance(serviceClazz.getClassLoader(), classes, this);
        transportMap.putIfAbsent(serviceClazz, transport);
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建Rpc请求参数
        RpcRequest rpcRequest = buildRpcRequest(method, args);
        // 调用远程服务
        // 并反序列化返回值，作为代理类的出参返回
        return SerializeSupport.parse(invokeRemote(rpcRequest, transportMap.get(method.getDeclaringClass())));
    }

    /**
     * 构建RPCRequest
     * @param method
     * @param args
     * @return
     */
    private RpcRequest buildRpcRequest(Method method, Object[] args) {
        // 默认只有一个入参
        byte[] bytes = SerializeSupport.serialize(args[0]);
        return new RpcRequest(method.getDeclaringClass().getCanonicalName(), method.getName(), bytes);
    }

    private byte[] invokeRemote(RpcRequest rpcRequest, Transport transport) {
        Command request = new Command();
        Header header = new Header(RequestIdSupport.next(), 1, ServiceTypes.TYPE_RPC_REQUEST);
        request.setHeader(header);
        request.setPayload(SerializeSupport.serialize(rpcRequest));
        try {
            CompletableFuture<Command> completableFuture = transport.send(request);
            Command responseCommand = completableFuture.get();
            ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();
            if (responseHeader.isSuccess()) {
                return responseCommand.getPayload();
            } else {
                throw new Exception(responseHeader.getMsg());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
