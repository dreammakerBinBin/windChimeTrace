package com.windchime.boot.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

@Slf4j
public class WindChimeAdvice extends WindChimeInterceptor implements MethodBeforeAdvice, AfterReturningAdvice {

    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
      log.info("WindChimeAdvice.before method start====="+method.getName());
    }


    @Override
    public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws Throwable {
        log.info("WindChimeAdvice.afterReturning method start====="+method.getName());

    }
}
