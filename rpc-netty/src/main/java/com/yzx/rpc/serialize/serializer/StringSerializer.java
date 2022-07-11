package com.yzx.rpc.serialize.serializer;

import java.nio.charset.Charset;

/**
 * @author baozi
 * @Description: String类型序列化
 * @Date created on 2022/7/10
 */
public class StringSerializer implements Serializer<String> {

    @Override
    public int size(String s) {
        return s.getBytes(Charset.defaultCharset()).length;
    }

    @Override
    public Class<String> getSerializeClass() {
        return String.class;
    }

    @Override
    public byte type() {
        return SerializeTypes.TYPE_STRING.getType();
    }

    @Override
    public void serialize(String obj, byte[] bytes, int offset, int length) {
        byte[] src = obj.getBytes(Charset.defaultCharset());
        System.arraycopy(src, 0, bytes, offset, length);
    }

    @Override
    public String parse(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, Charset.defaultCharset());
    }
}
