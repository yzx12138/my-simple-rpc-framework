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

    public static Command buildFailCommand(Command request, String msg) {
        Command failCommand = new Command();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setRequestId(request.getHeader().getRequestId());
        responseHeader.setType(request.getHeader().getType());
        responseHeader.setVersion(request.getHeader().getVersion());
        responseHeader.setCode(TransformConstants.ERROR_CODE);
        responseHeader.setMsg(msg);
        failCommand.setHeader(responseHeader);
        return failCommand;
    }

    public static Command buildSuccessCommand(Command request) {
        Command command = new Command();
        ResponseHeader responseHeader = new ResponseHeader();
        responseHeader.setRequestId(request.getHeader().getRequestId());
        responseHeader.setType(request.getHeader().getType());
        responseHeader.setVersion(request.getHeader().getVersion());
        responseHeader.setCode(TransformConstants.SUCCESS_CODE);
        command.setHeader(responseHeader);
        return command;
    }
}
