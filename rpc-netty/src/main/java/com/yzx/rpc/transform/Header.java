package com.yzx.rpc.transform;

import lombok.Data;

@Data
public class Header {

    /**
     * 请求id 做幂等
     */
    private Long requestId;

    /**
     * 请求版本 用于做传输协议老版本兼容和新版本的扩展
     */
    private Integer version;

    /**
     * 标识请求类型，以路由到具体的处理类中去
     */
    private Integer type;

    public Header(Long requestId, Integer version, Integer type) {
        this.requestId = requestId;
        this.version = version;
        this.type = type;
    }

    public Header() {
    }

    public int length() {
        return Long.BYTES + Integer.BYTES + Integer.BYTES;
    }
}