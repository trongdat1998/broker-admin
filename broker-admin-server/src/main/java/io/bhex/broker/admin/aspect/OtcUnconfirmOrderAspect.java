package io.bhex.broker.admin.aspect;


import io.bhex.broker.grpc.admin.NotifyType;
import io.bhex.ex.otc.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Aspect
@Service
public class OtcUnconfirmOrderAspect extends AbstractAspect {

    @Pointcut("@annotation(io.bhex.broker.admin.aspect.OtcUnconfirmOrderAnnotation)")
    public void getPointcut(){}

    @AfterReturning(pointcut="getPointcut()", returning="retVal")
    public void afterReturningAdvice(JoinPoint jp, Object retVal){

        Object[] args=jp.getArgs();
        log.info("OtcUnconfirmOrderAspect,arg size={}", Objects.nonNull(args)?args.length:0);
        if(Objects.nonNull(args)){
            OTCGetOrdersResponse resp=(OTCGetOrdersResponse) retVal;
            if(resp.getResult()!= OTCResult.SUCCESS){
                log.info("List unconfirm otc orders fail,resp={}", resp.getResult().name());
                return;
            }

            OTCGetOrdersRequest req=(OTCGetOrdersRequest)args[0];
            OTCOrderStatusEnum queryStatus=req.getOrderStatus(0);
            //检查是否查询未确认otc订单
            if(Objects.isNull(queryStatus) || queryStatus != OTCOrderStatusEnum.OTC_ORDER_UNCONFIRM){
                return;
            }
            Long brokerId=req.getBaseRequest().getOrgId();
            log.info("List unconfirm otc orders,brokerId={}",brokerId);
            notificationService.clearNotification(brokerId, NotifyType.OTC_UNCONFIRM);
        }
    }
}
