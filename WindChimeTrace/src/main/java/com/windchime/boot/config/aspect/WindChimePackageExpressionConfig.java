package com.windchime.boot.config.aspect;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Configuration
@Slf4j
public class WindChimePackageExpressionConfig {

    //切面配置package
    @Value("${windchime.trace.package:com.windchime.package}")
    private String defaultPackage;

//    @Autowired
//    @Lazy
//    private IWindChimeTraceConfigService iWindChimeTraceConfigService;

    //启动后会自动装配本Bean获取切点
    //此处可动态配置切点
    @Bean
    public Pointcut configurPointCut(){
        List<String> packages = Arrays.asList(defaultPackage.split(","));
        WindChimeConfigPointCut windChimeConfigPointCut = new WindChimeConfigPointCut();
//        windChimeConfigPointCut.setExpression("execution (public * com.hn.yfz.controller..*(..)) || execution (public * com.hn.yfz.serviceImpl..*(..))");
        windChimeConfigPointCut.setExpression(handleExecutionDefinition(packages));
        return windChimeConfigPointCut;
    }

    @Bean
    WindChimeAdvice getwindChimeAdvice(){
        return new WindChimeAdvice();
    }

    @Bean
    DefaultPointcutAdvisor defaultPointcutAdvisor(){
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        defaultPointcutAdvisor.setPointcut(configurPointCut());
        defaultPointcutAdvisor.setAdvice(getwindChimeAdvice());
        return defaultPointcutAdvisor;
    }



    //解析配置识别对应包
    private String handleExecutionDefinition(List<String> packages) {
        StringBuffer pointcutExpression = new StringBuffer();
        if (!CollectionUtil.isEmpty(packages)) {
            for(Iterator i$ = packages.iterator(); i$.hasNext(); pointcutExpression.append(") or ")) {
                String pkg = (String)i$.next();
                if(pkg.indexOf("!")>-1){
                    //mapper中使用mybatisplus，这部分的包不需要扫描 去除
                    pointcutExpression.delete(pointcutExpression.length() - 3, pointcutExpression.length()).append("&& ");
                    pkg = pkg.replace("!","");
                    pointcutExpression.append("!execution(* ").append(pkg);
                }else{
                    pointcutExpression.append("execution(* ").append(pkg);
                }
                if (pkg.indexOf("(") == -1) {
                    pointcutExpression.append("..*.*(..)");
                }
            }

            pointcutExpression.delete(pointcutExpression.length() - 4, pointcutExpression.length());
        }

        return pointcutExpression.toString();
    }


    @Scheduled(fixedRate = 60000, initialDelay = 3000)
    public void refreshTraceConfig() {
        log.info("refreshTraceConfig star=======");
//        List<WindChimeTraceConfig> windChimeTraceConfigs = iWindChimeTraceConfigService.list();
//        SingleTraceConfig.setTraceConfigCache(windChimeTraceConfigs);
        log.info("refreshTraceConfig end=======");
    }
}
