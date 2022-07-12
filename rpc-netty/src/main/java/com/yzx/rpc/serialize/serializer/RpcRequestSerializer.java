package com.yzx.rpc.serialize.serializer;

import com.yzx.rpc.transform.RpcRequest;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author baozi
 * @Description: RpcRequest序列化
 * @Date created on 2022/7/13
 */
public class RpcRequestSerializer implements Serializer<RpcRequest> {

    @Override
    public int size(RpcRequest rpcRequest) {
        return Integer.BYTES + rpcRequest.getInterfaceName().getBytes(Charset.defaultCharset()).length
        + Integer.BYTES + rpcRequest.getMethodName().getBytes(Charset.defaultCharset()).length
        + Integer.BYTES + rpcRequest.getSerializedArguments().length;
    }

    @Override
    public Class<RpcRequest> getSerializeClass() {
        return RpcRequest.class;
    }

    @Override
    public byte type() {
        return SerializeTypes.RPC_REQUEST.getType();
    }

    @Override
    public void serialize(RpcRequest obj, byte[] bytes, int offset, int length) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, offset, length);

        byte[] interfaceBytes = obj.getInterfaceName().getBytes(Charset.defaultCharset());
        byteBuffer.putInt(interfaceBytes.length);
        byteBuffer.put(interfaceBytes);

        byte[] methodBytes = obj.getMethodName().getBytes(Charset.defaultCharset());
        byteBuffer.putInt(methodBytes.length);
        byteBuffer.put(methodBytes);

        byte[] serializedArguments = obj.getSerializedArguments();
        byteBuffer.putInt(serializedArguments.length);
        byteBuffer.put(serializedArguments);
    }

    @Override
    public RpcRequest parse(byte[] bytes, int offset, int length) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, offset, length);
        byte[] interfaceBytes = new byte[byteBuffer.getInt()];
        byteBuffer.get(interfaceBytes);

        byte[] methodBytes = new byte[byteBuffer.getInt()];
        byteBuffer.get(methodBytes);

        byte[] serializedArguments = new byte[byteBuffer.getInt()];
        byteBuffer.get(serializedArguments);

        RpcRequest rpcRequest = new RpcRequest(new String(interfaceBytes, Charset.defaultCharset()),
                                               new String(methodBytes, Charset.defaultCharset()),
                                               serializedArguments);
        return rpcRequest;
    }
}
