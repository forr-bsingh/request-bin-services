package com.github.request.bin.utils;

import com.google.common.base.Stopwatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class LoggingAspect {

    private static final String LOG_TIMING = "Time taken to process the request : {}";
    private static final String LOG_ENTRY = "Entering {}()";
    private static final String LOG_EXIT = "Exiting {}()";

    @Around("@annotation(LogThis) && execution(public * *(..))")
    public Object logExecutionTime(ProceedingJoinPoint call) throws Throwable {
        Logger logger = LoggerFactory.getLogger(call.getSignature().getDeclaringType());
        MethodSignature signature = (MethodSignature) call.getSignature();
        LogThis logThis = signature.getMethod().getAnnotation(LogThis.class);
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        try {
            if (!stopwatch.isRunning()) {
                stopwatch.reset();
                stopwatch.start();
            }
            if (logThis.details() && logger.isInfoEnabled()) {
                logger = LoggerFactory.getLogger(call.getSignature().getDeclaringType());
                logger.info(LOG_ENTRY, ((MethodSignature) call.getSignature()).getMethod().getName());
            }
            Object object = call.proceed();
            if (logThis.details() && logger.isInfoEnabled()) {
                logger = LoggerFactory.getLogger(call.getSignature().getDeclaringType());
                logger.info(LOG_EXIT, ((MethodSignature) call.getSignature()).getMethod().getName());
            }
            return object;
        } finally {
            if (stopwatch.isRunning()) {
                stopwatch.stop();
            }
            if (logThis.timing() && logger.isInfoEnabled()) {
                logger = LoggerFactory.getLogger(call.getSignature().getDeclaringType());
                logger.info(LOG_TIMING, stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
            // Nullify the object to make it eligible for garbage collection.
            logger = null;
        }
    }
}
