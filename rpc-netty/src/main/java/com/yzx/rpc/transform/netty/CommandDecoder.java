package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.Command;
import com.yzx.rpc.transform.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author baozi
 * @Description: Command转码回结构化对象
 * @Date created on 2022/7/12
 */
public abstract class CommandDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
            throws Exception {
        // 获取整个字节数组数据的长度
        int length = byteBuf.readInt();

        Header header = decodeHeader(byteBuf);
        byte[] payload = new byte[length - header.length()];
        byteBuf.readBytes(payload);

        Command command = new Command();
        command.setHeader(header);
        command.setPayload(payload);
        list.add(command);
    }

    /**
     * 解码Header
     * 由于Header支持版本升级和兼容，因此Header的解码也可以抽象出来
     * @param byteBuf
     * @return
     */
    public abstract Header decodeHeader(ByteBuf byteBuf);
}
