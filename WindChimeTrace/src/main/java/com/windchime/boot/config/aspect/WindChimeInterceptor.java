package com.windchime.boot.config.aspect;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.util.ClassUtil;
import com.alibaba.fastjson.JSONObject;
import com.windchime.boot.pojo.windchimetraceinfo.WindChimeTraceInfo;
import com.windchime.boot.config.WindChimeTraceServicePersistence;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Lazy;

import javax.servlet.http.HttpServletRequest;
import java.beans.Introspector;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;


@Slf4j
public class WindChimeInterceptor implements MethodInterceptor, Serializable {

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;
    @Autowired
    @Lazy
    private WindChimeTraceServicePersistence windChimeTraceServicePersistence;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object result=null;
        WindChimeTraceInfo windChimeTraceInfo=null;
        try{
            if (((ReflectiveMethodInvocation) methodInvocation).getThis().getClass().getName().indexOf("$$EnhancerBySpringCGLIB$$") > -1) {
                //cglib动态代理的类也被拦截 直接放掉
                return methodInvocation.proceed();
            }
            windChimeTraceInfo = this.prepareServiceTraceInfo(methodInvocation);
        }catch (Exception e){
            log.error("[windChimeTrace][WindChimeInterceptor]exception-detail:{}",e.getCause());
        }
        result =  methodInvocation.proceed();
        try {
            windChimeTraceInfo.setResult(result);
            windChimeTraceInfo.setStartTime(new Date());
            windChimeTraceInfo.setRequestIp(getIpAddress(httpServletRequest));
            windChimeTraceServicePersistence.saveWindChimeTraceInfo(windChimeTraceInfo,getHeaders(httpServletRequest));
        }catch (Exception e){
            log.error("[windChimeTrace][WindChimeInterceptor],exception-detail:{}",e.getCause());
        }
       return result;
    }


    private WindChimeTraceInfo prepareServiceTraceInfo(MethodInvocation methodInvocation) {
        WindChimeTraceInfo windChimeTraceInfo = new WindChimeTraceInfo();
        windChimeTraceInfo.setServiceName(this.methodSignature(methodInvocation.getMethod()));
        windChimeTraceInfo.setArguments(methodInvocation.getArguments());
        return windChimeTraceInfo;
    }

    public String methodSignature(Method method) {
        Alias alias = (Alias)method.getAnnotation(Alias.class);
        return null != alias ? alias.value() : this.methodSignatureByServiceName(method);
    }

    public String methodSignatureByServiceName(Method method) {
        Class<?> realClass = this.getRealClass(method.getDeclaringClass());
        String[] names = this.defaultListableBeanFactory.getBeanNamesForType(realClass);
        String serviceName;
        if (null != names && names.length > 0) {
            serviceName = names[0];
        } else {
            serviceName = Introspector.decapitalize(method.getDeclaringClass().getName());
        }

        return serviceName + "." + method.getName();
    }

    private Class<?> getRealClass(Class<?> realClass) {
        return ClassUtil.getClass(realClass);
    }


    public static String getIpAddress(HttpServletRequest request) {
        String ip = "";

        try {
            ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception var3) {
            //很多非web请求也呗拦截 不打印
//            var3.printStackTrace();
        }

        return ip;
    }

    protected String getHeaders(HttpServletRequest httpServletRequest){
        JSONObject jsonObject = null;
        try {
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
            jsonObject = new JSONObject();
            while(headerNames.hasMoreElements()) {//判断是否还有下一个元素
                String element = headerNames.nextElement();//获取headerNames集合中的请求头
                String headerVal = httpServletRequest.getHeader(element);//通过请求头得到请求内容
                jsonObject.put(element,headerVal);
            }
            return jsonObject.toJSONString();
        }catch (Exception e){}
        return "";
    }

}
