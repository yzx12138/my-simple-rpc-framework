package com.yzx.rpc.server;

import com.yzx.rpc.client.ServiceTypes;
import com.yzx.rpc.serialize.SerializeSupport;
import com.yzx.rpc.spi.Singleton;
import com.yzx.rpc.transform.Command;
import com.yzx.rpc.transform.RpcRequest;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baozi
 * @Description: Rpc请求处理器
 * @Date created on 2022/7/13
 */
@Singleton
public class RpcRequestHandler implements RequestHandler, ServiceProviderRegistry {

    private final Map<String/*serviceName*/, Object/*serviceProvider*/> serviceProviders = new ConcurrentHashMap();

    @Override
    public Integer getType() {
        return ServiceTypes.TYPE_RPC_REQUEST;
    }

    /**
     * 处理远程请求的方法
     * 1.根据类名路由到具体的实现类
     * 2.通过反射，调用实际的实现类逻辑
     * 3.返回结果
     */
    @Override
    public Command handle(Command request) {
        byte[] payload = request.getPayload();
        // 默认只有一个String类型的入参
        try {
            RpcRequest rpcRequest = SerializeSupport.parse(payload);
            Object serviceProvider = serviceProviders.get(rpcRequest.getInterfaceName());
            if (serviceProvider == null) {
                return Command.buildFailCommand(request, "serviceProvider not exist");
            }
            String param = SerializeSupport.parse(rpcRequest.getSerializedArguments());
            Method method = serviceProvider.getClass().getMethod(rpcRequest.getMethodName(), String.class);
            String res = (String) method.invoke(serviceProvider, param);
            Command command = Command.buildSuccessCommand(request);
            command.setPayload(SerializeSupport.serialize(res));
            return command;
        } catch (Throwable e) {
            return Command.buildFailCommand(request, e.getMessage());
        }
    }

    @Override
    public <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider) {
        serviceProviders.put(serviceClass.getCanonicalName(), serviceProvider);
    }
}
