package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.PushAdminClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.app_push.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 15:53
 */
@Slf4j
@Service
public class PushAdminClientImpl implements PushAdminClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Override
    public SendAdminTestPushResponse sendAdminTestPush(SendAdminTestPushRequest request) {
        AppPushServiceGrpc.AppPushServiceBlockingStub stub = grpcConfig.appPushServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);

        return stub.sendAdminTestPush(request);
    }

    @Override
    public AddAdminPushTaskResponse addAdminPushTask(AddAdminPushTaskRequest request) {
        AppPushServiceGrpc.AppPushServiceBlockingStub stub = grpcConfig.appPushServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);

        return stub.addAdminPushTask(request);
    }

    @Override
    public EditAdminPushTaskResponse editAdminPushTask(EditAdminPushTaskRequest request) {
        AppPushServiceGrpc.AppPushServiceBlockingStub stub = grpcConfig.appPushServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);

        return stub.editAdminPushTask(request);
    }

    @Override
    public QueryAdminPushTaskResponse queryAdminPushTask(QueryAdminPushTaskRequest request) {
        AppPushServiceGrpc.AppPushServiceBlockingStub stub = grpcConfig.appPushServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        return stub.queryAdminPushTask(request);
    }

    @Override
    public DeleteAdminPushTaskResponse deleteAdminPushTask(DeleteAdminPushTaskRequest request) {
        AppPushServiceGrpc.AppPushServiceBlockingStub stub = grpcConfig.appPushServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        return stub.deleteAdminPushTask(request);
    }

    @Override
    public CancelAdminPushTaskResponse cancelAdminPushTask(CancelAdminPushTaskRequest request) {
        AppPushServiceGrpc.AppPushServiceBlockingStub stub = grpcConfig.appPushServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        return stub.cancelAdminPushTask(request);
    }

    @Override
    public QueryAdminPushTaskSimplesResponse queryAdminPushTaskSimples(QueryAdminPushTaskSimplesRequest request) {
        AppPushServiceGrpc.AppPushServiceBlockingStub stub = grpcConfig.appPushServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        return stub.queryAdminPushTaskSimples(request);
    }
}
