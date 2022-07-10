package com.yzx.rpc.serialize.serializer;

/**
 * @author baozi
 * @Description: 序列化接口
 * @Date created on 2022/7/10
 */
public interface Serializer<T> {

    /**
     * 获取待序列化对象长度，用于申请字节数组
     * @param t
     * @return
     */
    int size(T t);

    /**
     * 获取该Serializer序列化的类
     * @return
     */
    Class<T> getSerializeClass();

    /**
     * 返回序列化对象的类型，也要放入字节数组中去
     * @return
     */
    byte type();

    /**
     * 序列化
     * @param obj 待序列化对象
     * @param bytes 输出字节数组
     * @param offset 输出的偏移量
     * @param length 序列化后的长度
     */
    void serialize(T obj, byte[] bytes, int offset, int length);

    /**
     * 反序列化
     * @param bytes
     * @param offset
     * @param length
     * @return
     */
    T parse(byte[] bytes, int offset, int length);
}
