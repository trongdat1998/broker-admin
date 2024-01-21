package io.bhex.broker.admin.aspect;


import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.grpc.client.NotificationService;
import io.bhex.broker.grpc.admin.NotifyType;
import io.bhex.ex.otc.OTCHandleOrderRequest;
import io.bhex.ex.otc.OTCHandleOrderResponse;
import io.bhex.ex.otc.OTCResult;
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
public class WithdrawHandleAspect extends AbstractAspect{


    @Pointcut("@annotation(io.bhex.broker.admin.aspect.WithdrawHandleAnnotation)")
    public void withdrawHandlePointcut(){}

    @AfterReturning(pointcut="withdrawHandlePointcut()", returning="retVal")
    public void afterReturningAdvice(JoinPoint jp, Object retVal){

        Object[] args=jp.getArgs();
        if(Objects.nonNull(args)&&args.length>0){
/*            ResultModel resp=(ResultModel) retVal;
            if(resp.getCode()!= 0){
                return;
            }*/

            Long brokerId=(Long)args[0];
            notificationService.clearNotification(brokerId, NotifyType.WITHDRAW);
        }
    }
}
