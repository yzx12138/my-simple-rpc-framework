package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.Header;
import com.yzx.rpc.transform.ResponseHeader;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * @author baozi
 * @Description: ResponseEncoder
 * @Date created on 2022/7/12
 */
public class ResponseEncoder extends CommandEncoder {

    @Override
    public void encodeHeader(Header header, ByteBuf byteBuf) {
        ResponseHeader responseHeader = (ResponseHeader) header;
        byteBuf.writeInt(header.getType());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeLong(header.getRequestId());

        byteBuf.writeInt(responseHeader.getCode());
        // msg长度
        byteBuf.writeInt(responseHeader.length()
                         - Integer.BYTES - Integer.BYTES - Long.BYTES
                         - Integer.BYTES - Integer.BYTES);
        byteBuf.writeBytes(responseHeader.getMsg() == null ?
                                   new byte[0] :
                                   responseHeader.getMsg().getBytes(Charset.defaultCharset()));
    }
}
