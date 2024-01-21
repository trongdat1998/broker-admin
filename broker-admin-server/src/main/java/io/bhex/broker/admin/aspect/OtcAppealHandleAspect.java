package io.bhex.broker.admin.aspect;


import io.bhex.broker.admin.grpc.client.NotificationService;
import io.bhex.broker.grpc.admin.NotifyType;
import io.bhex.ex.otc.*;
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
public class OtcAppealHandleAspect extends AbstractAspect{


    @Pointcut("@annotation(io.bhex.broker.admin.aspect.OtcAppealHandleAnnotation)")
    public void otcAppealHandlePointcut(){}

/*    @AfterReturning(pointcut="otcAppealHandlePointcut()", returning="retVal")
    public void afterReturningAdvice(JoinPoint jp, Object retVal){

        Object[] args=jp.getArgs();
        log.info("OtcAppealHandleAspect,arg size={}",Objects.nonNull(args)?args.length:0);
        if(Objects.nonNull(args)){
            OTCHandleOrderResponse resp=(OTCHandleOrderResponse) retVal;
            if(resp.getResult()!= OTCResult.SUCCESS){
                log.info("Handle otc appeal fail,resp={}", resp.getResult().toString());
                return;
            }

            OTCHandleOrderRequest req=(OTCHandleOrderRequest)args[0];
            Long brokerId=req.getBaseRequest().getOrgId();
            log.info("Handle otc appeal,brokerId={}",brokerId);
            notificationService.decreNotification(brokerId, NotifyType.OTC_APPEAL);
        }
    }*/

    @AfterReturning(pointcut="otcAppealHandlePointcut()", returning="retVal")
    public void afterReturningAdvice(JoinPoint jp, Object retVal){

        Object[] args=jp.getArgs();
        log.info("OtcAppealHandleAspect,arg size={}", Objects.nonNull(args)?args.length:0);
        if(Objects.nonNull(args)){
            OTCGetOrdersResponse resp=(OTCGetOrdersResponse) retVal;
            if(resp.getResult()!= OTCResult.SUCCESS){
                log.info("List otc orders fail,resp={}", resp.getResult().name());
                return;
            }

            OTCGetOrdersRequest req=(OTCGetOrdersRequest)args[0];

            Long brokerId=req.getBaseRequest().getOrgId();
            log.info("Clean otc appeal,brokerId={}",brokerId);
            notificationService.clearNotification(brokerId, NotifyType.OTC_APPEAL);
        }
    }
}
