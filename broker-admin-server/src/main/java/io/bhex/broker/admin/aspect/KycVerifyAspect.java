package io.bhex.broker.admin.aspect;


import io.bhex.broker.admin.grpc.client.NotificationService;
import io.bhex.broker.grpc.admin.NotifyType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Aspect
@Service
public class KycVerifyAspect extends AbstractAspect{


    @Pointcut("@annotation(io.bhex.broker.admin.aspect.KycVerifyAnnotation)")
    public void kycVerifyPointcut(){}

    @AfterReturning(pointcut="kycVerifyPointcut()", returning="retVal")
    public void afterReturningAdvice(JoinPoint jp, Object retVal){

        Object[] args = jp.getArgs();
        if (Objects.nonNull(args)) {
            log.info("clear kyc notification...");

            Long brokerId=(Long)args[2];
            notificationService.clearNotification(brokerId, NotifyType.KYC);
        }
    }
}
