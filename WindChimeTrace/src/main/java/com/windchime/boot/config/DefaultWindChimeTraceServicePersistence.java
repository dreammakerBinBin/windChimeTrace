package com.windchime.boot.config;

import com.alibaba.fastjson.JSONObject;
import com.windchime.boot.pojo.windchimetraceconfig.WindChimeTraceConfigPojo;
import com.windchime.boot.pojo.windchimetraceinfo.WindChimeTraceInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
public class DefaultWindChimeTraceServicePersistence extends AbstracttWindChimeTraceServicePersistence{
    @Override
    protected void doSaveWindChimeTraceInfo(WindChimeTraceInfo windChimeTraceInfo, String headersJsonVal) {
        log.info("[windChimeTrace][log]DefaultWindChimeTraceServicePersistence.doSaveWindChimeTraceInfo start====");
        List<WindChimeTraceConfigPojo> windChimeTraceConfigPojos = SingleTraceConfig.getTraceConfigMap();
        windChimeTraceConfigPojos.forEach(config->{
            WindChimeTraceServiceLog windChimeTraceServiceLog = windChimeTraceInfo.getServiceName().equals(config.getConfigMethod())
                    && moduleName.equals(config.getEnableModule())
                    && "1".equals(config.getIsEnable())
                    ? WindChimeTraceServiceLog.builder()
                    .createTime(new Date())
                    .funModule(moduleName)
                    .operateMethod(windChimeTraceInfo.getServiceName())
                    .operateParams("1".equals(config.getIsParamEnable())?handleInput(windChimeTraceInfo.getArguments()):null)
                    .result("1".endsWith(config.getIsOutputEnable())?handleOutput(windChimeTraceInfo.getResult()):null)
                    .operateIp(windChimeTraceInfo.getRequestIp())
                    .errorInfo(headersJsonVal)
                    .build()
                    : null;
            if(windChimeTraceServiceLog == null){
                return;
            }
            rabbitProducer.sendApiTraceLogMsg(JSONObject.toJSONString(windChimeTraceServiceLog));
        log.info("[windChimeTrace][log]DefaultWindChimeTraceServicePersistence.doSaveWindChimeTraceInfo end====");
    }

    @Override
    public void refreshTraceConfig() {
        log.info("[windChimeTrace][log]DefaultWindChimeTraceServicePersistence.refreshTraceConfig start====");
        log.info("[windChimeTrace][log]DefaultWindChimeTraceServicePersistence.refreshTraceConfig end====");

    }
}
