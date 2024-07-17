package com.windchime.boot.pojo.windchimetraceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 存储本次方法栈信息
 */
@Data
public class WindChimeTraceInfo implements Serializable {
    private String serviceName;
    private Object[] arguments;
    private Object result;
    private String module;
    private Date startTime;
    private Date endTime;
    private String requestIp;
}
