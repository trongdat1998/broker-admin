package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.admin.common.BrokerAccountTradeFeeSettingServiceGrpc;
import io.bhex.base.admin.common.BrokerTradeFeeSettingServiceGrpc;
import io.bhex.base.admin.common.DeleteAccountFeeRateAdjustRequest;
import io.bhex.base.admin.common.DeleteAccountFeeRateAdjustResponse;
import io.bhex.base.admin.common.DisableBrokerAccountTradeFeeGroupRequest;
import io.bhex.base.admin.common.DisableBrokerAccountTradeFeeGroupResponse;
import io.bhex.base.admin.common.EditBrokerAccountTradeFeeGroupRequest;
import io.bhex.base.admin.common.EditBrokerAccountTradeFeeGroupResponse;
import io.bhex.base.admin.common.EnableBrokerAccountTradeFeeGroupRequest;
import io.bhex.base.admin.common.EnableBrokerAccountTradeFeeGroupResponse;
import io.bhex.base.admin.common.GetBrokerAccountTradeFeeGroupRequest;
import io.bhex.base.admin.common.GetBrokerAccountTradeFeeGroupResponse;
import io.bhex.base.admin.common.GetBrokerAccountTradeFeeGroupsRequest;
import io.bhex.base.admin.common.GetBrokerAccountTradeFeeGroupsResponse;
import io.bhex.base.admin.common.SaveBrokerAccountTradeFeeDetailsRequest;
import io.bhex.base.admin.common.SaveBrokerAccountTradeFeeDetailsResponse;
import io.bhex.base.admin.common.UpdateAccountFeeRateAdjustRequest;
import io.bhex.base.admin.common.UpdateAccountFeeRateAdjustResponse;
import io.bhex.base.admin.common.UpdateSendStatusRequest;
import io.bhex.base.admin.common.UpdateSendStatusResponse;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.grpc.client.BrokerAccountTradeFeeSettingClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @Date: 2018/11/21 下午5:32
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@Service
public class BrokerAccountTradeFeeSettingClientImpl implements BrokerAccountTradeFeeSettingClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private BrokerAccountTradeFeeSettingServiceGrpc.BrokerAccountTradeFeeSettingServiceBlockingStub getStub(){
        return grpcConfig.brokerAccountTradeFeeSettingServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }


    @Override
    public GetBrokerAccountTradeFeeGroupResponse getBrokerAccountTradeFeeGroup(GetBrokerAccountTradeFeeGroupRequest request) {
        return getStub().getBrokerAccountTradeFeeGroup(request);
    }

    @Override
    public EditBrokerAccountTradeFeeGroupResponse editBrokerAccountTradeFeeGroup(EditBrokerAccountTradeFeeGroupRequest request) {
        return getStub().editBrokerAccountTradeFeeGroup(request);
    }

    @Override
    public EnableBrokerAccountTradeFeeGroupResponse enableBrokerAccountTradeFeeGroup(EnableBrokerAccountTradeFeeGroupRequest request) {
        return getStub().enableBrokerAccountTradeFeeGroup(request);
    }

    @Override
    public DisableBrokerAccountTradeFeeGroupResponse disableBrokerAccountTradeFeeGroup(DisableBrokerAccountTradeFeeGroupRequest request) {
        return getStub().disableBrokerAccountTradeFeeGroup(request);
    }

    @Override
    public GetBrokerAccountTradeFeeGroupsResponse getBrokerAccountTradeFeeGroups(GetBrokerAccountTradeFeeGroupsRequest request) {
        return getStub().getBrokerAccountTradeFeeGroups(request);
    }

//    @Override
//    public GetExistedAccountIdsResponse getExistedAccountIds(GetExistedAccountIdsRequest request) {
//        return getStub().getExistedAccountIds(request);
//    }

    @Override
    public SaveBrokerAccountTradeFeeDetailsResponse saveBrokerAccountTradeFeeDetails(SaveBrokerAccountTradeFeeDetailsRequest request) {
        return getStub().saveBrokerAccountTradeFeeDetails(request);
    }

    @Override
    public UpdateSendStatusResponse updateSendStatus(UpdateSendStatusRequest request) {
        return getStub().updateSendStatus(request);
    }

    @Override
    public UpdateAccountFeeRateAdjustResponse updateAccountFeeRateAdjust(UpdateAccountFeeRateAdjustRequest request) {
        BrokerTradeFeeSettingServiceGrpc.BrokerTradeFeeSettingServiceBlockingStub stub =
                grpcConfig.brokerTradeFeeSettingServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        return stub.updateAccountFeeRateAdjust(request);
    }

    @Override
    public DeleteAccountFeeRateAdjustResponse deleteAccountFeeRateAdjust(DeleteAccountFeeRateAdjustRequest request) {
        BrokerTradeFeeSettingServiceGrpc.BrokerTradeFeeSettingServiceBlockingStub stub =
                grpcConfig.brokerTradeFeeSettingServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        return stub.deleteAccountFeeRateAdjust(request);
    }
}
