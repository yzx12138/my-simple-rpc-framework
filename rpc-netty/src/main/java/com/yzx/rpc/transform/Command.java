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


    @Data
    class Header {

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
    }

    @Data
    class RequestHeader extends Header {

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
    }
}
