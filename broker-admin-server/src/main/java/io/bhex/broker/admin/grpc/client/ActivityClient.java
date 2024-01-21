package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.activity.lockInterest.ActivityOrderTaskToFailRequest;
import io.bhex.broker.grpc.activity.lockInterest.ActivityOrderTaskToFailResponse;
import io.bhex.broker.grpc.activity.lockInterest.CreateActivityOrderTaskRequest;
import io.bhex.broker.grpc.activity.lockInterest.ExecuteActivityOrderTaskRequest;
import io.bhex.broker.grpc.activity.lockInterest.ExecuteActivityOrderTaskResponse;
import io.bhex.broker.grpc.activity.lockInterest.ModifyActivityOrderInfoRequest;
import io.bhex.broker.grpc.activity.lockInterest.ModifyActivityOrderInfoResponse;
import io.bhex.broker.grpc.activity.lockInterest.QueryActivityProjectInfoRequest;
import io.bhex.broker.grpc.activity.lockInterest.QueryActivityProjectInfoResponse;
import io.bhex.broker.grpc.activity.lockInterest.CreateActivityOrderTaskResponse;
import io.bhex.broker.grpc.activity.lockInterest.CreateActivityOrderTaskResponse;


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

public interface ActivityClient {

    SaveActivityReply createActivity(SaveActivityRequest request);

    ListActivityReply listActivity(ListActivityRequest request);

    FindActivityReply findActivity(FindActivityRequest request);

    AdminCommonResponse calculateActivityResult(CalculateActivityRequest request);

    FindActivityResultReply findActivityResult(FindActivityRequest request);

    AdminCommonResponse confirmActivityResult(FindActivityRequest request);

    AdminCommonResponse onlineActivity(OnlineRequest request);

    ListActivityOrderReply listActivityOrder(FindActivityRequest request);

    AdminQueryAllActivityOrderInfoReply adminQueryAllActivityOrderInfo(AdminQueryAllActivityOrderInfoRequest request);

    QueryIeoWhiteListReply queryIeoWhiteList(QueryIeoWhiteListRequest request);

    SaveIeoWhiteListReply saveIeoWhiteList(SaveIeoWhiteListRequest request);

    ModifyActivityOrderInfoResponse modifyActivityOrderInfo(ModifyActivityOrderInfoRequest request);

    QueryActivityProjectInfoResponse queryActivityProjectInfo(QueryActivityProjectInfoRequest request);

    //创建自由分配任务
    CreateActivityOrderTaskResponse createActivityOrderTask(CreateActivityOrderTaskRequest request);

    //执行自由分配任务
    ExecuteActivityOrderTaskResponse executeActivityOrderTask(ExecuteActivityOrderTaskRequest request);

    //上传数据失败处理
    ActivityOrderTaskToFailResponse activityOrderTaskToFail(ActivityOrderTaskToFailRequest request);
}
