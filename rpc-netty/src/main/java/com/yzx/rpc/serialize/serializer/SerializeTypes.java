package com.yzx.rpc.serialize.serializer;

/**
 * @author baozi
 * @Description: 序列化类型枚举
 * @Date created on 2022/7/10
 */
public enum SerializeTypes {

    TYPE_STRING((byte) 1, String.class)
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
