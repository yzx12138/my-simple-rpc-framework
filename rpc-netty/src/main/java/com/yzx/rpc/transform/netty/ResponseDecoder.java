package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.Header;
import com.yzx.rpc.transform.ResponseHeader;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * @author baozi
 * @Description: ResponseDecoder
 * @Date created on 2022/7/12
 */
public class ResponseDecoder extends CommandDecoder{

    @Override
    public Header decodeHeader(ByteBuf byteBuf) {
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setType(byteBuf.readInt());
        responseHeader.setVersion(byteBuf.readInt());
        responseHeader.setRequestId(byteBuf.readLong());

        responseHeader.setCode(byteBuf.readInt());
        // msg长度
        int length = byteBuf.readInt();
        byte[] msgBytes = new byte[length];
        byteBuf.readBytes(msgBytes);
        responseHeader.setMsg(new String(msgBytes, Charset.defaultCharset()));
        return responseHeader;
    }
}
