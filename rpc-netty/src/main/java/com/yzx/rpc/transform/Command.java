package com.yzx.rpc.transform;

import lombok.Data;

/**
 * @author baozi
 * @Description: 命令
 * @Date created on 2022/7/11
 */
@Data
public class Command {

    /**
     * 命令头
     */
    private Header header;

    /**
     * 传输信息 字节数组
     */
    private byte[] payload;

}
