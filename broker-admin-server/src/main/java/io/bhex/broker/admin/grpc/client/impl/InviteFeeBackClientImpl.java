package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.InviteFeeBackClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.invite.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Service
public class InviteFeeBackClientImpl implements InviteFeeBackClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private InviteServiceGrpc.InviteServiceBlockingStub getStub() {
        return grpcConfig.inviteServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public GetAdminInviteBonusRecordResponse getAdminInviteBonusRecord(long orgId, long statisticsTime, long userId, String token, int page, int limit) {
        GetAdminInviteBonusRecordRequest request = GetAdminInviteBonusRecordRequest.newBuilder()
                .setPage(page)
                .setLimit(limit)
                .setUserId(userId)
                .setToken(token)
                .setStatisticsTime(statisticsTime)
                .setOrgId(orgId)
                .build();
        GetAdminInviteBonusRecordResponse response = getStub().getAdminInviteBonusRecord(request);
        return response;
    }

    @Override
    public InitInviteFeeBackActivityResponse initInviteFeeBackActivity(Long orgId) {
        InitInviteFeeBackActivityRequest request = InitInviteFeeBackActivityRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        InitInviteFeeBackActivityResponse response = getStub().initInviteFeeBackActivity(request);
        log.info("request:{} response:{}", request, response);
        return response;
    }

    @Override
    public GetInviteFeeBackActivityResponse getInviteFeeBackActivity(Long orgId) {

        GetInviteFeeBackActivityRequest request = GetInviteFeeBackActivityRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        GetInviteFeeBackActivityResponse response = getStub().getInviteFeeBackActivity(request);
        log.info("request:{} response:{}", request, response);
        return response;
    }


    @Override
    public UpdateInviteFeeBackLevelResponse updateInviteFeeBackLevel(Long orgId, Long actId, Long levelId,
                                                                     Integer levelCondition, BigDecimal directRate, BigDecimal indirectRate) {
        UpdateInviteFeeBackLevelRequest request = UpdateInviteFeeBackLevelRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setActId(actId)
                .setLevelId(levelId)
                .setLevelCondition(String.valueOf(levelCondition))
                .setDirectRate(directRate.toPlainString())
                .setIndirectRate(indirectRate.toPlainString())
                .build();
        UpdateInviteFeeBackLevelResponse response = getStub().updateInviteFeeBackLevel(request);
        //log.info("request:{} response:{}", request, response);
        return response;

    }

    @Override
    public UpdateInviteFeeBackActivityResponse updateInviteFeeBackActivity(Long orgId, Long actId, Integer status, Integer coinStatus, Integer futuresStatus) {
        UpdateInviteFeeBackActivityRequest request = UpdateInviteFeeBackActivityRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setActId(actId)
                .setStatus(status)
                .setCoinStatus(coinStatus)
                .setFuturesStatus(futuresStatus)
                .build();
        UpdateInviteFeeBackActivityResponse response = getStub().updateInviteFeeBackActivity(request);
        log.info("request:{} response:{}", request, response);
        return response;
    }

    @Override
    public GetInviteBlackListResponse getInviteBlackList(GetInviteBlackListRequest request) {
        return getStub().getInviteBlackList(request);
    }

    @Override
    public AddInviteBlackListResponse addInviteBlackList(AddInviteBlackListRequest request) {
        return getStub().addInviteBlackList(request);
    }

    @Override
    public DeleteInviteBlackListResponse deleteInviteBlackList(DeleteInviteBlackListRequest request) {
        return getStub().deleteInviteBlackList(request);
    }

    @Override
    public UpdateInviteFeebackPeriodResponse updateInviteFeebackPeriod(UpdateInviteFeebackPeriodRequest request) {
        return getStub().updateInviteFeebackPeriod(request);
    }

    @Override
    public UpdateInviteFeebackAutoTransferResponse updateInviteFeebackAutoTransfer(UpdateInviteFeebackAutoTransferRequest request) {
        return getStub().updateInviteFeebackAutoTransfer(request);
    }

    @Override
    public GetInviteStatisticsRecordListResponse getInviteStatisticsRecordList(GetInviteStatisticsRecordListRequest request) {
        return getStub().getInviteStatisticsRecordList(request);
    }

    @Override
    public GetDailyTaskListResponse getDailyTaskList(GetDailyTaskListRequest request) {
        return getStub().getDailyTaskList(request);
    }

    @Override
    public ExecuteAdminGrantInviteBonusResponse executeAdminGrantInviteBonus(ExecuteAdminGrantInviteBonusRequest request) {
        return getStub().executeAdminGrantInviteBonus(request);
    }

    @Override
    public GetInviteCommonSettingResponse getInviteCommonSetting(GetInviteCommonSettingRequest request) {
        return getStub().getInviteCommonSetting(request);
    }

    @Override
    public UpdateInviteCommonSettingResponse updateInviteCommonSetting(UpdateInviteCommonSettingRequest request) {
        return getStub().updateInviteCommonSetting(request);
    }

    @Override
    public GenerateAdminInviteBonusRecordResponse generateAdminInviteBonusRecord(GenerateAdminInviteBonusRecordRequest request) {
        return getStub().generateAdminInviteBonusRecord(request);
    }

    @Override
    public void initInviteWechatConfig(Long orgId) {
        getStub().initInviteWechatConfig(InitInviteWechatConfigRequest.newBuilder().setOrgId(orgId).build());
    }
}
