package com.yzx.rpc.serialize;

import com.yzx.rpc.serialize.serializer.SerializeTypes;
import com.yzx.rpc.serialize.serializer.Serializer;
import com.yzx.rpc.serialize.serializer.StringSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baozi
 * @Description: 序列化
 * @Date created on 2022/7/10
 */
public class SerializeSupport {

    public static void main(String[] args) {
        String testSrl = "Test测试ceshi。。。，，，...,,,";
        byte[] serializeBytes = SerializeSupport.serialize(testSrl);
        String parse = SerializeSupport.parse(serializeBytes);
        System.out.println(testSrl.equals(parse));
    }

    /**
     * 用于确认一个类用那个序列化实现类
     */
    private static Map<Class<?>/*待序列化的类*/, Serializer<?>/*序列化实现类*/> clazzSerializerMap = new HashMap<>();

    /**
     * 用于反序列化时，根据byte查询对应的类
     */
    private static Map<Byte/*序列化后的类型*/, Class<?>/*序列化前的类*/> classByteMap = new HashMap<>();

    static {
        Serializer<String> StringSerializer = new StringSerializer();
        clazzSerializerMap.put(String.class, StringSerializer);
        classByteMap.put(SerializeTypes.TYPE_STRING.getType(), String.class);
    }

    public static <T> byte[] serialize(T obj) {
        Class<?> clazz = obj.getClass();
        Serializer<T> serializer = (Serializer<T>) clazzSerializerMap.get(clazz);
        if (serializer == null) {
            throw new UnsupportedOperationException("serializer not exist, class unSupport");
        }

        int size = serializer.size(obj);
        byte[] bytes = new byte[size+1];
        bytes[0] = serializer.type();
        serializer.serialize(obj, bytes, 1, size);

        return bytes;
    }

    public static <T> T parse(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("bytes should not be empty");
        }
        byte type = bytes[0];
        Class<?> clazz = classByteMap.get(type);
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown type");
        }
        Serializer<T> serializer = (Serializer<T>) clazzSerializerMap.get(clazz);

        return serializer.parse(bytes, 1, bytes.length - 1);
    }

}
