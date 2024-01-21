package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.ActivityClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.activity.lockInterest.ActivityLockInterestServiceGrpc;
import io.bhex.broker.grpc.activity.lockInterest.ActivityOrderTaskToFailRequest;
import io.bhex.broker.grpc.activity.lockInterest.ActivityOrderTaskToFailResponse;
import io.bhex.broker.grpc.activity.lockInterest.CreateActivityOrderTaskRequest;
import io.bhex.broker.grpc.activity.lockInterest.CreateActivityOrderTaskResponse;
import io.bhex.broker.grpc.activity.lockInterest.ExecuteActivityOrderTaskRequest;
import io.bhex.broker.grpc.activity.lockInterest.ExecuteActivityOrderTaskResponse;
import io.bhex.broker.grpc.activity.lockInterest.ModifyActivityOrderInfoRequest;
import io.bhex.broker.grpc.activity.lockInterest.ModifyActivityOrderInfoResponse;
import io.bhex.broker.grpc.activity.lockInterest.QueryActivityProjectInfoRequest;
import io.bhex.broker.grpc.activity.lockInterest.QueryActivityProjectInfoResponse;
import io.bhex.broker.grpc.admin.AdminActivityServiceGrpc;
import io.bhex.broker.grpc.admin.AdminQueryAllActivityOrderInfoReply;
import io.bhex.broker.grpc.admin.AdminQueryAllActivityOrderInfoRequest;
import io.bhex.broker.grpc.admin.CalculateActivityRequest;
import io.bhex.broker.grpc.admin.FindActivityReply;
import io.bhex.broker.grpc.admin.FindActivityRequest;
import io.bhex.broker.grpc.admin.FindActivityResultReply;
import io.bhex.broker.grpc.admin.ListActivityOrderReply;
import io.bhex.broker.grpc.admin.ListActivityReply;
import io.bhex.broker.grpc.admin.ListActivityRequest;
import io.bhex.broker.grpc.admin.OnlineRequest;
import io.bhex.broker.grpc.admin.QueryIeoWhiteListReply;
import io.bhex.broker.grpc.admin.QueryIeoWhiteListRequest;
import io.bhex.broker.grpc.admin.SaveActivityReply;
import io.bhex.broker.grpc.admin.SaveActivityRequest;
import io.bhex.broker.grpc.admin.SaveIeoWhiteListReply;
import io.bhex.broker.grpc.admin.SaveIeoWhiteListRequest;
import io.bhex.broker.grpc.proto.AdminCommonResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ActivityClientImpl implements ActivityClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminActivityServiceGrpc.AdminActivityServiceBlockingStub getStub() {
        return grpcConfig.adminActivityServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private ActivityLockInterestServiceGrpc.ActivityLockInterestServiceBlockingStub activityLockInterestServiceBlockingStub() {
        return grpcConfig.activityLockInterestServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }


    @Override
    public SaveActivityReply createActivity(SaveActivityRequest request) {
        return getStub().saveActivity(request);
    }

    @Override
    public ListActivityReply listActivity(ListActivityRequest request) {
        return getStub().listActivity(request);
    }

    @Override
    public FindActivityReply findActivity(FindActivityRequest request) {
        return getStub().findActivity(request);
    }

    @Override
    public AdminCommonResponse calculateActivityResult(CalculateActivityRequest request) {
        return getStub().calculateActivityResult(request);
    }

    @Override
    public FindActivityResultReply findActivityResult(FindActivityRequest request) {
        return getStub().findActivityResult(request);
    }

    @Override
    public AdminCommonResponse confirmActivityResult(FindActivityRequest request){
        return getStub().confirmActivityResult(request);
    }

    @Override
    public AdminCommonResponse onlineActivity(OnlineRequest request){
        return getStub().onlineActivity(request);
    }

    @Override
    public ListActivityOrderReply listActivityOrder(FindActivityRequest request) {
        return getStub().listActivityOrder(request);
    }

    @Override
    public AdminQueryAllActivityOrderInfoReply adminQueryAllActivityOrderInfo(AdminQueryAllActivityOrderInfoRequest request) {
        return getStub().adminQueryAllActivityOrderInfo(request);
    }

    @Override
    public QueryIeoWhiteListReply queryIeoWhiteList(QueryIeoWhiteListRequest request) {
        return getStub().queryIeoWhiteList(request);
    }

    @Override
    public SaveIeoWhiteListReply saveIeoWhiteList(SaveIeoWhiteListRequest request) {
        return getStub().saveIeoWhiteList(request);
    }

    @Override
    public ModifyActivityOrderInfoResponse modifyActivityOrderInfo(ModifyActivityOrderInfoRequest request) {
        return activityLockInterestServiceBlockingStub().modifyActivityOrderInfo(request);
    }

    @Override
    public QueryActivityProjectInfoResponse queryActivityProjectInfo(QueryActivityProjectInfoRequest request) {
        return activityLockInterestServiceBlockingStub().queryActivityProjectInfo(request);
    }

    @Override
    public CreateActivityOrderTaskResponse createActivityOrderTask(CreateActivityOrderTaskRequest request) {
        return activityLockInterestServiceBlockingStub().createActivityOrderTask(request);
    }

    @Override
    public ExecuteActivityOrderTaskResponse executeActivityOrderTask(ExecuteActivityOrderTaskRequest request) {
        return activityLockInterestServiceBlockingStub().executeActivityOrderTask(request);
    }

    @Override
    public ActivityOrderTaskToFailResponse activityOrderTaskToFail(ActivityOrderTaskToFailRequest request) {
        return activityLockInterestServiceBlockingStub().activityOrderTaskToFail(request);
    }
}
