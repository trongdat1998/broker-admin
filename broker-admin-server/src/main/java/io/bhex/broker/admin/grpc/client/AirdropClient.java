package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.*;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 10/11/2018 9:38 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface AirdropClient {

    Integer STATUS_INIT = 10;
    Integer STATUS_SUCCESS = 1;
    Integer STATUS_AIRDOP = 2;
    Integer STATUS_FAILED = 3;
    Integer STATUS_PART_SUCCESS = 4;
    Integer STATUS_CLOSED = 5;
    Integer STATUS_AUDIT_PASSED = 14; //审核通过，可进行空投
    Integer STATUS_FAILED_INSUFFICIENT = 31;


    QueryAirdropInfoReply queryAirdropInfo(QueryAirdropInfoRequest request);

    QueryAirdropInfoReply listScheduleAirdrop();

    CreateAirdropInfoReply createAirdropInfo(CreateAirdropInfoRequest request);

    UpdateAirdropStatusReply updateAirdropStatus(UpdateAirdropStatusRequest request);

    ListAllTransferGroupReply listAllTransferGroup(ListAllTransferGroupRequest request);

    TransferGroupInfo getTransferGroupInfo(GetTransferGroupInfoRequest request);

    UpdateTransferGroupStatusReply updateTransferGroupStatus(UpdateTransferGroupStatusRequest request);

    ListTransferRecordReply listTransferRecordByGroupId(ListTransferRecordRequest request);

    AirdropInfo getAirdropInfo(GetAirdropInfoRequest request);

    LockAndAirdropReply lockAndAirdrop(LockAndAirdropRequest request);

    AddTransferRecordReply addTransferRecord(AddTransferRecordRequest request);

    AddTmplRecordReply addTmplRecord(AddTmplRecordRequest request);

    ListTmplRecordsReply listTmplRecords(ListTmplRecordsRequest request);

    TransferRecordFilterReply transferRecordFilter(TransferRecordFilterRequest request);

    AddAssetSnapshotReply addAssetSnapshot(AddAssetSnapshotRequest request);

}
