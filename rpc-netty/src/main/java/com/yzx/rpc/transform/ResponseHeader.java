package com.yzx.rpc.transform;

import lombok.Data;

@Data
public class ResponseHeader extends Header {

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String msg;

    public boolean isSuccess() {
        return code != null && TransformConstants.SUCCESS_CODE.equals(code);
    }

    public ResponseHeader(Long requestId, Integer version, Integer type, Integer code, String msg) {
        super(requestId, version, type);
        this.code = code;
        this.msg = msg;
    }
}