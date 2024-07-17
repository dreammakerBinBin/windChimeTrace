package com.windchime.boot.config;


//import com.windchime.boot.pojo.windchimetraceconfig.WindChimeTraceConfig;
import com.windchime.boot.pojo.windchimetraceconfig.WindChimeTraceConfigPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 懒汉式单例类
 * 保证全局配置唯一
 */
public class SingleTraceConfig {

    private static List<WindChimeTraceConfigPojo> traceConfigCache ;


    public static synchronized List<WindChimeTraceConfigPojo> getTraceConfigMap(){
        if(traceConfigCache==null){
            synchronized (SingleTraceConfig.class){
                return new ArrayList<WindChimeTraceConfigPojo>();
            }
        }
        return traceConfigCache;
    }

    public static void setTraceConfigCache(List<WindChimeTraceConfigPojo> traceConfigCache) {
        SingleTraceConfig.traceConfigCache = traceConfigCache;
    }
}
