package com.windchime.boot.config;

import com.alibaba.fastjson.JSON;
//import com.windchime.boot.config.rabbitmq.RabbitProducer;
import com.alibaba.fastjson.JSONObject;
import com.windchime.boot.pojo.windchimetraceinfo.WindChimeTraceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

//import com.baiyyy.yfz.config.mq.RabbitProducer;

//@Component
@Slf4j
//@EnableAsync
public  abstract class AbstracttWindChimeTraceServicePersistence implements WindChimeTraceServicePersistence{



//    @Autowired
//    private RabbitProducer producer;
//
//    @Value("${spring.application.name:windchime}")
//    private String moduleName;

    protected abstract void doSaveWindChimeTraceInfo(WindChimeTraceInfo windChimeTraceInfo,String headersJsonVal);

    @Override
    public void saveWindChimeTraceInfo(WindChimeTraceInfo windChimeTraceInfo, String headersJsonVal) {
        //保存配置了的方法的日志信息
        log.info("[windChimeTrace][log]saveWindChimeTraceInfo start====");

        //发送给mq
        doSaveWindChimeTraceInfo(windChimeTraceInfo,headersJsonVal);
        log.info("[windChimeTrace][log]saveWindChimeTraceInfo after doSaveWindChimeTraceInfo====");
        log.info("[windChimeTrace][log]saveWindChimeTraceInfo end====");
//        List<WindChimeTraceConfig> windChimeTraceConfig = SingleTraceConfig.getTraceConfigMap();
//        windChimeTraceConfig.forEach(config->{
//            WindChimeTraceServiceLog windChimeTraceServiceLog = windChimeTraceInfo.getServiceName().equals(config.getConfigMethod())
//                    && moduleName.equals(config.getEnableModule())
//                    && "1".equals(config.getIsEnable())
//                ? WindChimeTraceServiceLog.builder()
//                    .createTime(new Date())
//                    .funModule(moduleName)
//                    .operateMethod(windChimeTraceInfo.getServiceName())
//                    .operateParams("1".equals(config.getIsParamEnable())?handleInput(windChimeTraceInfo.getArguments()):null)
//                    .result("1".endsWith(config.getIsOutputEnable())?handleOutput(windChimeTraceInfo.getResult()):null)
//                    .operateIp(windChimeTraceInfo.getRequestIp())
//                    .errorInfo(headersJsonVal)
//                    .build()
//                : null;
//            if(windChimeTraceServiceLog == null){
//                return;
//            }
////            iWindChimeTraceServiceLogService.save(windChimeTraceServiceLog);
//            producer.sendMsg(JSONObject.toJSONString(windChimeTraceServiceLog));
//        });
    }



    protected String handleInput(Object[] arguments) {
        if (null != arguments && arguments.length != 0) {
            if (arguments.length == 1) {
                return this.handleOutput(arguments[0]);
            } else {
                Map<String, Object> params = new LinkedHashMap();

                for(int i = 0; i < arguments.length; ++i) {
                    params.put("param" + (i + 1), this.isWebParam(arguments[i]) ? arguments[i].getClass().getSimpleName() + " skipped." : arguments[i]);
                }

                return JSON.toJSONString(params);
            }
        } else {
            return null;
        }
    }

    protected String handleOutput(Object returnVal) {
        if (null != returnVal) {
            if (returnVal instanceof String) {
                return (String)returnVal;
            } else {
                return this.isWebParam(returnVal) ? returnVal.getClass().getSimpleName() + " skipped." : JSON.toJSONString(returnVal);
            }
        } else {
            return null;
        }
    }

    protected boolean isWebParam(Object object) {
        if (null != object && false) {
            String clsName = object.getClass().getName();
            return clsName.startsWith("javax.servlet") || clsName.startsWith("org.apache.catalina") || clsName.startsWith("org.springframework") || this.isClassImplementsOfServlet(object.getClass());
        } else {
            return false;
        }
    }

    protected boolean isClassImplementsOfServlet(Class<?> aClass) {
        Class<?>[] interfaces = aClass.getInterfaces();
        if (null != interfaces && interfaces.length > 0) {
            Class[] arr$ = interfaces;
            int len$ = interfaces.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Class<?> intf = arr$[i$];
                if (intf.getName().startsWith("javax.servlet")) {
                    return true;
                }

                if (this.isClassImplementsOfServlet(intf)) {
                    return true;
                }
            }
        }

        return aClass.getSuperclass() != Object.class ? this.isClassImplementsOfServlet(aClass.getSuperclass()) : false;
    }
}
