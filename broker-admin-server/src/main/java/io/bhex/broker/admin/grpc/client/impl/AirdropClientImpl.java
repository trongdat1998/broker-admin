package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.AirdropClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.AddAssetSnapshotReply;
import io.bhex.broker.grpc.admin.AddAssetSnapshotRequest;
import io.bhex.broker.grpc.admin.AddTmplRecordReply;
import io.bhex.broker.grpc.admin.AddTmplRecordRequest;
import io.bhex.broker.grpc.admin.AddTransferRecordReply;
import io.bhex.broker.grpc.admin.AddTransferRecordRequest;
import io.bhex.broker.grpc.admin.AdminAirdropServiceGrpc;
import io.bhex.broker.grpc.admin.AirdropInfo;
import io.bhex.broker.grpc.admin.CreateAirdropInfoReply;
import io.bhex.broker.grpc.admin.CreateAirdropInfoRequest;
import io.bhex.broker.grpc.admin.GetAirdropInfoRequest;
import io.bhex.broker.grpc.admin.GetTransferGroupInfoRequest;
import io.bhex.broker.grpc.admin.ListAllTransferGroupReply;
import io.bhex.broker.grpc.admin.ListAllTransferGroupRequest;
import io.bhex.broker.grpc.admin.ListScheduleAirdropRequest;
import io.bhex.broker.grpc.admin.ListTmplRecordsReply;
import io.bhex.broker.grpc.admin.ListTmplRecordsRequest;
import io.bhex.broker.grpc.admin.ListTransferRecordReply;
import io.bhex.broker.grpc.admin.ListTransferRecordRequest;
import io.bhex.broker.grpc.admin.LockAndAirdropReply;
import io.bhex.broker.grpc.admin.LockAndAirdropRequest;
import io.bhex.broker.grpc.admin.QueryAirdropInfoReply;
import io.bhex.broker.grpc.admin.QueryAirdropInfoRequest;
import io.bhex.broker.grpc.admin.TransferGroupInfo;
import io.bhex.broker.grpc.admin.TransferRecordFilterReply;
import io.bhex.broker.grpc.admin.TransferRecordFilterRequest;
import io.bhex.broker.grpc.admin.UpdateAirdropStatusReply;
import io.bhex.broker.grpc.admin.UpdateAirdropStatusRequest;
import io.bhex.broker.grpc.admin.UpdateTransferGroupStatusReply;
import io.bhex.broker.grpc.admin.UpdateTransferGroupStatusRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 10/11/2018 9:39 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class AirdropClientImpl implements AirdropClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminAirdropServiceGrpc.AdminAirdropServiceBlockingStub getAirdropStub() {
        return grpcConfig.adminAirdropServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public QueryAirdropInfoReply queryAirdropInfo(QueryAirdropInfoRequest request) {
        return getAirdropStub().queryAirdropInfo(request);
    }

    @Override
    public QueryAirdropInfoReply listScheduleAirdrop() {
        ListScheduleAirdropRequest request = ListScheduleAirdropRequest.newBuilder()
                .build();
        return getAirdropStub().listScheduleAirdrop(request);
    }

    @Override
    public CreateAirdropInfoReply createAirdropInfo(CreateAirdropInfoRequest request) {
        return getAirdropStub().createAirdropInfo(request);
    }

    @Override
    public AirdropInfo getAirdropInfo(GetAirdropInfoRequest request) {
        return getAirdropStub().getAirdropInfo(request);
    }

    @Override
    public LockAndAirdropReply lockAndAirdrop(LockAndAirdropRequest request) {
        return getAirdropStub().lockAndAirdrop(request);
    }

    @Override
    public AddTransferRecordReply addTransferRecord(AddTransferRecordRequest request) {
        return getAirdropStub().addTransferRecord(request);
    }

    @Override
    public AddTmplRecordReply addTmplRecord(AddTmplRecordRequest request) {
        return getAirdropStub().addTmplRecord(request);
    }

    @Override
    public ListTmplRecordsReply listTmplRecords(ListTmplRecordsRequest request) {
        return getAirdropStub().listTmplRecords(request);
    }

    @Override
    public AddAssetSnapshotReply addAssetSnapshot(AddAssetSnapshotRequest request) {
        return getAirdropStub().addAssetSnapshot(request);
    }

    @Override
    public UpdateAirdropStatusReply updateAirdropStatus(UpdateAirdropStatusRequest request) {
        return getAirdropStub().updateAirdropStatus(request);
    }

    @Override
    public TransferGroupInfo getTransferGroupInfo(GetTransferGroupInfoRequest request) {
        return getAirdropStub().getTransferGroupInfo(request);
    }

    @Override
    public TransferRecordFilterReply transferRecordFilter(TransferRecordFilterRequest request) {
        return getAirdropStub().transferRecordFilter(request);
    }

    @Override
    public ListAllTransferGroupReply listAllTransferGroup(ListAllTransferGroupRequest request) {
        return getAirdropStub().listAllTransferGroup(request);
    }

    @Override
    public UpdateTransferGroupStatusReply updateTransferGroupStatus(UpdateTransferGroupStatusRequest request) {
        return getAirdropStub().updateTransferGroupStatus(request);
    }

    @Override
    public ListTransferRecordReply listTransferRecordByGroupId(ListTransferRecordRequest request) {
        return getAirdropStub().listTransferRecordByGroupId(request);
    }
}
