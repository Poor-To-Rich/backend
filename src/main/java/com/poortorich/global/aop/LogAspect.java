package com.poortorich.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Slf4j
@Component
public class LogAspect {

    @Pointcut("execution(* com.poortorich..*.*(..)) && " +
            "!execution(* com.poortorich.global..*(..)) && " +
            "!execution(* com.poortorich.security..*(..))")
    public void all() {
    }

    @Pointcut("execution(* com.poortorich..controller..*(..))")
    public void controller() {
    }

    @Around("all()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("[START] [{}] {}", className, methodName);

        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("[ERROR] [{}] {} : {}", className, methodName, e.getMessage());
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            long timeInMs = end - start;
            log.info("[END] [{}] {} | {}ms", className, methodName, timeInMs);
        }
    }

    @Around("controller()")
    public Object loggingBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();

        String controllerName = joinPoint.getSignature().getDeclaringType().getName();
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> params = new HashMap<>();

        try {
            params.put("controller", controllerName);
            params.put("method", methodName);
            params.put("params", getParams(request));
            params.put("log_time", System.currentTimeMillis());
            params.put("request_uri", request.getRequestURI());
            params.put("http_method", request.getMethod());
        } catch (Exception e) {
            log.error("[ERROR] : {}", e.getMessage());
        }

        String ip = request.getRemoteAddr();

        log.info("");
        log.info("[REQUEST] [{}] {}", params.get("http_method"), params.get("request_uri"));
        log.info("[METHOD] {}.{}", params.get("controller") ,params.get("method"));
        log.info("[PARAMS] {}", params.get("params"));
        log.info("[IP] {}", ip);

        return joinPoint.proceed();
    }

    private Object getParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            paramMap.put(param.replaceAll("\\.", "-"), request.getParameter(param));
        }
        return paramMap;
    }
}
