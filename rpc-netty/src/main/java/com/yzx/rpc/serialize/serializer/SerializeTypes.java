package com.yzx.rpc.serialize.serializer;

import com.yzx.rpc.name.service.MetaData;
import com.yzx.rpc.transform.RpcRequest;

/**
 * @author baozi
 * @Description: 序列化类型枚举
 * @Date created on 2022/7/10
 */
public enum SerializeTypes {

    TYPE_STRING((byte) 1, String.class),
    META_DATA((byte) 2, MetaData.class),
    RPC_REQUEST((byte) 3, RpcRequest.class)
    ;

    private byte type;
    private Class clazz;

    SerializeTypes(byte type, Class clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public byte getType() {
        return type;
    }

    public Class getClazz() {
        return clazz;
    }
}
