package io.bhex.broker.admin.grpc.client.impl;

import com.google.common.collect.Maps;
import io.bhex.broker.admin.grpc.client.NotificationService;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.proto.AdminCommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Resource
    GrpcClientConfig grpcConfig;

    private Map<String, String> notifyTypeMap = Maps.newHashMap();

    @PostConstruct
    public void init() {
        notifyTypeMap.put("kyc-application","kyc");
        notifyTypeMap.put("otc-appeal","otc");
        notifyTypeMap.put("withdraw","withdraw");
        notifyTypeMap.put("otc-unconfirm","otc-unconfirm");
    }

    @Override
    public Map<String,Integer> listNotification(long userId, long brokerId) {

        try{
            AdminBrokerNotifyServiceGrpc.AdminBrokerNotifyServiceBlockingStub stub=
                    grpcConfig.adminBrokerNotifyServiceBlockingStub(GrpcClientConfig.ADMIN_COMMON_GRPC_CHANNEL_NAME);

            ListNotificationRequest req=ListNotificationRequest.newBuilder().setBrokerId(brokerId).setUserId(userId).build();
            ListNotificationResponse resp = stub.listNotification(req);
            if(resp.getResult()){
                return resp.getNotificationList().stream().map(i->{
                    //NotificationDTO dto=new NotificationDTO();
                    return Pair.of(notifyTypeMap.get(i.getNotifyType()),i.getNumber());
                }).collect(Collectors.toMap(i->i.getKey()+"",i->i.getValue()));
            }
            log.error("NotificationServiceImpl:org:{} user:{} err:{}", brokerId, userId,resp.getMessage());
            throw new IllegalArgumentException(resp.getMessage());
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void decreNotification(long brokerId, NotifyType notifyType) {

        try{
            AdminBrokerNotifyServiceGrpc.AdminBrokerNotifyServiceBlockingStub stub=
                    grpcConfig.adminBrokerNotifyServiceBlockingStub(GrpcClientConfig.ADMIN_COMMON_GRPC_CHANNEL_NAME);

            //log.info("decreNotification,brokerId={},notifyType={}",brokerId,notifyType.toString());
            DecreNotificationRequest req= DecreNotificationRequest.newBuilder()
                    .setBrokerId(brokerId).setNotifyType(notifyType).build();
            AdminCommonResponse resp = stub.decreNotification(req);
            //log.info("decreNotification resp={},msg={}", resp.getSuccess(),resp.getMsg());
            if(resp.getSuccess()){
                //log.info("decreNotification,success");
                return;
            }

            log.error(resp.getMsg());
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

    @Override
    public void clearNotification(Long brokerId, NotifyType notifyType) {

        try{
            AdminBrokerNotifyServiceGrpc.AdminBrokerNotifyServiceBlockingStub stub=
                    grpcConfig.adminBrokerNotifyServiceBlockingStub(GrpcClientConfig.ADMIN_COMMON_GRPC_CHANNEL_NAME);

            //log.info("clearNotification,brokerId={},notifyType={}",brokerId,notifyType.toString());
            DecreNotificationRequest req= DecreNotificationRequest.newBuilder()
                    .setBrokerId(brokerId).setNotifyType(notifyType).build();
            AdminCommonResponse resp = stub.clearNotification(req);
            //log.info("clearNotification resp={},msg={}", resp.getSuccess(),resp.getMsg());
            if(resp.getSuccess()){
                //log.info("clearNotification,success");
                return;
            }

            log.error(resp.getMsg());
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }
}
