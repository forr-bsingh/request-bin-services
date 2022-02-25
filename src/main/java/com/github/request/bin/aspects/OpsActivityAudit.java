package com.github.request.bin.aspects;

import com.github.request.bin.data.Ops;
import com.github.request.bin.repos.AuditRepo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
public class OpsActivityAudit {

    private final AuditRepo auditRepo;

    public OpsActivityAudit(AuditRepo auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Around(value = "execution(* com.github.request.bin.services.impl.OpsServiceImpl.postOps(..))")
    public Object auditPostOp(ProceedingJoinPoint call) throws Throwable {
        Object object = null;
        String bin = (String) call.getArgs()[0];
        Map<String, Object> request = (Map<String, Object>) call.getArgs()[1];
        Ops ops = auditRepo.create(bin, "POST", request, null, call.getArgs()[2]);
        try {
            object = call.proceed(call.getArgs());
        } catch (Throwable throwable) {
            object = ExceptionUtils.getMessage(throwable);
            throw throwable;
        } finally {
            ops = auditRepo.get(bin, ops.getIdentifier());
            if (Objects.nonNull(ops)) {
                ops.setResponse(object);
                auditRepo.update(bin, ops.getIdentifier(), ops);
            }
        }
        return object;
    }

    @Around(value = "execution(* com.github.request.bin.services.impl.OpsServiceImpl.getOps(..))")
    public Object auditGetOp(ProceedingJoinPoint call) throws Throwable {
        Object object = null;
        String bin = (String) call.getArgs()[0];
        Map<String, Object> request = new HashMap<>();
        request.put("id", call.getArgs()[1]);
        Ops ops = auditRepo.create(bin, "GET", request, null, call.getArgs()[2]);
        try {
            object = call.proceed(call.getArgs());
        } catch (Throwable throwable) {
            object = ExceptionUtils.getMessage(throwable);
            throw throwable;
        } finally {
            ops = auditRepo.get(bin, ops.getIdentifier());
            if (Objects.nonNull(ops)) {
                ops.setResponse(object);
                auditRepo.update(bin, ops.getIdentifier(), ops);
            }
        }
        return object;
    }

    @Around(value = "execution(* com.github.request.bin.services.impl.OpsServiceImpl.listOps(..))")
    public Object auditListOp(ProceedingJoinPoint call) throws Throwable {
        Object object = null;
        String bin = (String) call.getArgs()[0];
        Map<String, Object> request = new HashMap<>();
        request.put("p", call.getArgs()[1]);
        request.put("l", call.getArgs()[2]);
        Ops ops = auditRepo.create(bin, "GET", request, null, call.getArgs()[3]);
        try {
            object = call.proceed(call.getArgs());
        } catch (Throwable throwable) {
            object = ExceptionUtils.getMessage(throwable);
            throw throwable;
        } finally {
            ops = auditRepo.get(bin, ops.getIdentifier());
            if (Objects.nonNull(ops)) {
                ops.setResponse(object);
                auditRepo.update(bin, ops.getIdentifier(), ops);
            }
        }
        return object;
    }

    @Around(value = "execution(* com.github.request.bin.services.impl.OpsServiceImpl.deleteOps(..))")
    public Object auditDeleteOp(ProceedingJoinPoint call) throws Throwable {
        Object object = null;
        String bin = (String) call.getArgs()[0];
        Map<String, Object> request = new HashMap<>();
        request.put("id", call.getArgs()[1]);
        Ops ops = auditRepo.create(bin, "DELETE", request, null, call.getArgs()[2]);
        try {
            object = call.proceed(call.getArgs());
        } catch (Throwable throwable) {
            object = ExceptionUtils.getMessage(throwable);
            throw throwable;
        } finally {
            ops = auditRepo.get(bin, ops.getIdentifier());
            if (Objects.nonNull(ops)) {
                ops.setResponse(object);
                auditRepo.update(bin, ops.getIdentifier(), ops);
            }
        }
        return object;
    }

    @Around(value = "execution(* com.github.request.bin.services.impl.OpsServiceImpl.patchOps(..))")
    public Object auditPatchOp(ProceedingJoinPoint call) throws Throwable {
        Object object = null;
        String bin = (String) call.getArgs()[0];
        Map<String, Object> request = new HashMap<>();
        request.put("id", call.getArgs()[1]);
        request.put("payload", call.getArgs()[2]);
        Ops ops = auditRepo.create(bin, "PATCH", request, null, call.getArgs()[3]);
        try {
            object = call.proceed(call.getArgs());
        } catch (Throwable throwable) {
            object = ExceptionUtils.getMessage(throwable);
            throw throwable;
        } finally {
            ops = auditRepo.get(bin, ops.getIdentifier());
            if (Objects.nonNull(ops)) {
                ops.setResponse(object);
                auditRepo.update(bin, ops.getIdentifier(), ops);
            }
        }
        return object;
    }

    @Around(value = "execution(* com.github.request.bin.services.impl.OpsServiceImpl.putOps(..))")
    public Object auditPutOp(ProceedingJoinPoint call) throws Throwable {
        Object object = null;
        String bin = (String) call.getArgs()[0];
        Map<String, Object> request = new HashMap<>();
        request.put("id", call.getArgs()[1]);
        request.put("payload", call.getArgs()[2]);
        Ops ops = auditRepo.create(bin, "PUT", request, null, call.getArgs()[3]);
        try {
            object = call.proceed(call.getArgs());
        } catch (Throwable throwable) {
            object = ExceptionUtils.getMessage(throwable);
            throw throwable;
        } finally {
            ops = auditRepo.get(bin, ops.getIdentifier());
            if (Objects.nonNull(ops)) {
                ops.setResponse(object);
                auditRepo.update(bin, ops.getIdentifier(), ops);
            }
        }
        return object;
    }
}
