package com.yzx.rpc.name.service;

import java.util.Objects;

/**
 * @author baozi
 * @Description: 注册中心支持的协议
 * @Date created on 2022/7/12
 */
public enum NameServiceSchemes {

    FILE(1, "file")
    ;

    private Integer code;
    private String desc;

    NameServiceSchemes(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (NameServiceSchemes value : values()) {
            if (Objects.equals(value.code, code)) {
                return value.getDesc();
            }
        }
        return "";
    }

    public boolean isCurrent(Integer code) {
        return code != null && Objects.equals(this.code, code);
    }
}
