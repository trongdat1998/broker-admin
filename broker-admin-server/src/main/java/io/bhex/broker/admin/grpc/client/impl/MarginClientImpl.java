package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.MarginClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.margin.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MarginClientImpl implements MarginClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Override
    public SetTokenConfigResponse setTokenConfig(SetTokenConfigRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).setTokenConfig(request);
    }

    @Override
    public GetTokenConfigResponse queryMarginTokenConfig(String tokenId, Long orgId) {
        MarginServiceGrpc.MarginServiceBlockingStub stub = grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        GetTokenConfigRequest.Builder builder = GetTokenConfigRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTokenId(tokenId);
        return stub.getTokenConfig(builder.build());
    }

    @Override
    public SetRiskConfigResponse setRiskConfig(SetRiskConfigRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).setRiskConfig(request);
    }

    @Override
    public GetRiskConfigResponse queryRiskConfig(Long orgId) {
        MarginServiceGrpc.MarginServiceBlockingStub stub = grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        GetRiskConfigRequest.Builder builder = GetRiskConfigRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build());
        return stub.getRiskConfig(builder.build());
    }

    @Override
    public SetInterestConfigResponse setInterestConfig(SetInterestConfigRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).setInterestConfig(request);
    }

    @Override
    public GetInterestConfigResponse queryInterestConfig(String tokenId, Long orgId) {
        MarginServiceGrpc.MarginServiceBlockingStub stub = grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        GetInterestConfigRequest.Builder builder = GetInterestConfigRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTokenId(tokenId);
        return stub.getInterestConfig(builder.build());
    }

    @Override
    public SetMarginSymbolResponse setMarginSymbol(SetMarginSymbolRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).setMarginSymbol(request);
    }

    @Override
    public GetPoolAccountResponse getPoolAccount(Long orgId) {
        MarginServiceGrpc.MarginServiceBlockingStub stub = grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        GetPoolAccountRequest.Builder builder = GetPoolAccountRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build());
        return stub.getPoolAccount(builder.build());
    }

    @Override
    public GetCrossLoanOrderResponse queryCrossLoanOrder(GetCrossLoanOrderRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).getCrossLoanOrder(request);
    }

    @Override
    public GetRepayRecordResponse queryRepayRecord(GetRepayRecordRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).getRepayRecord(request);
    }

    @Override
    public QueryCoinPoolResponse queryCoinPool(QueryCoinPoolRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryCoinPool(request);
    }

    @Override
    public QueryUserRiskResponse queryUserRisk(QueryUserRiskRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryUserRisk(request);
    }

    @Override
    public StatisticsUserRiskResponse statisticsUserRisk(StatisticsUserRiskRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).statisticsUserRisk(request);
    }

    @Override
    public ForceCloseResponse forceClose(ForceCloseRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).forceClose(request);
    }

    @Override
    public StatisticsRiskResponse statisticsRisk(StatisticsRiskRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).statisticsRisk(request);
    }

    @Override
    public QueryRptDailyStatisticsRiskResponse queryRptDailyStatisticsRisk(QueryRptDailyStatisticsRiskRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryRptDailyStatisticsRisk(request);
    }

    @Override
    public QueryUserPositionResponse queryPositionDetail(QueryUserPositionRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryPositionDetail(request);
    }

    @Override
    public QueryForceCloseResponse queryForceClose(QueryForceCloseRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryForceClose(request);
    }

    @Override
    public QueryInterestByLevelResponse queryInteresyBtLevel(QueryInterestByLevelRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryInterestByLevel(request);
    }

    @Override
    public AdminQueryForceRecordResponse adminQueryForceRecord(AdminQueryForceRecordRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminQueryForceRecord(request);
    }

    @Override
    public AdminQueryAccountStatusResponse adminQueryAccountStatus(AdminQueryAccountStatusRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminQueryAccountStatus(request);
    }

    @Override
    public AdminChangeMarginPositionStatusResponse adminChangeMarginPosition(AdminChangeMarginPositionStatusRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminChangeMarginPositionStatus(request);
    }

    @Override
    public QueryAccountLoanLimitVIPResponse queryAccountLoanLimitVIP(QueryAccountLoanLimitVIPRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryAccountLoanLimitVIP(request);
    }

    @Override
    public SetAccountLoanLimitVIPResponse setAccountLoanLimitVIP(SetAccountLoanLimitVIPRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).setAccountLoanLimitVIP(request);
    }

    @Override
    public DeleteAccountLoanLimitVIPResponse deleteAccountLoanLimitVIP(DeleteAccountLoanLimitVIPRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).deleteAccountLoanLimitVIP(request);
    }

    @Override
    public QueryRptMarginPoolResponse queryRptMarginPool(QueryRptMarginPoolRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryRptMarginPool(request);
    }

    @Override
    public QueryMarginRiskBlackListResponse queryMarginRiskBlackList(QueryMarginRiskBlackListRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryMarginRiskBlackList(request);
    }

    @Override
    public AddMarginRiskBlackListResponse addMarginRiskBlackList(AddMarginRiskBlackListRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).addMarginRiskBlackList(request);
    }

    @Override
    public DelMarginRiskBlackListResponse delMarginRiskBlackList(DelMarginRiskBlackListRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).delMarginRiskBlackList(request);
    }

    @Override
    public QueryRptMarginTradeResponse queryRptMarginTrade(QueryRptMarginTradeRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryRptMarginTrade(request);
    }

    @Override
    public QueryRptMarginTradeDetailResponse queryRptMarginTradeDetail(QueryRptMarginTradeDetailRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryRptMarginTradeDetail(request);
    }

    @Override
    public SetSpecialInterestResponse setSpecialInterest(SetSpecialInterestRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).setSpecialInterest(request);
    }

    @Override
    public QuerySpecialInterestResponse querySpecialInterest(QuerySpecialInterestRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).querySpecialInterest(request);
    }

    @Override
    public DeleteSpecialInterestResponse deleteSpecialInterest(DeleteSpecialInterestRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).deleteSpecialInterest(request);
    }

    @Override
    public AdminQueryOpenMarginActivityResponse queryOpenMarginActivity(AdminQueryOpenMarginActivityRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminQueryOpenMarginActivity(request);
    }

    @Override
    public AdminCheckDayOpenMarginActivityResponse adminCheckDayOpenMarginActivity(AdminCheckDayOpenMarginActivityRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminCheckDayOpenMarginActivity(request);
    }

    @Override
    public AdminCheckMonthOpenMarginActivityResponse adminCheckMonthOpenMarginActivity(AdminCheckMonthOpenMarginActivityRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminCheckMonthOpenMarginActivity(request);
    }

    @Override
    public AdminQueryProfitActivityResponse adminQueryProfitActivity(AdminQueryProfitActivityRequest request) {
        return grpcConfig.marginPositionServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminQueryProfitActivity(request);
    }

    @Override
    public AdminSetProfitRankingResponse adminSetProfitRanking(AdminSetProfitRankingRequest request) {
        return grpcConfig.marginPositionServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminSetProfitRanking(request);
    }

    @Override
    public AdminSortProfitRankingResponse adminSortProfitRanking(AdminSortProfitRankingRequest request) {
        return grpcConfig.marginPositionServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminSortProfitRanking(request);
    }

    @Override
    public AdminRecalTopProfitRateResponse adminRecalTopProfitRate(AdminRecalTopProfitRateRequst request) {
        return grpcConfig.marginPositionServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminRecalTopProfitRate(request);
    }

    @Override
    public AdminSetSpecialLoanLimitResponse adminSetSpecialLoanLimit(AdminSetSpecialLoanLimitRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminSetSpecialLoanLimit(request);
    }

    @Override
    public AdminQuerySpecialLoanLimitResponse adminQuerySpecialLoanLimit(AdminQuerySpecialLoanLimitRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminQuerySpecialLoanLimit(request);
    }

    @Override
    public AdminDelSpecialLoanLimitResponse adminDelSpecialLoanLimit(AdminDelSpecialLoanLimitRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminDelSpecialLoanLimit(request);
    }

    @Override
    public AdminSetMarginLoanLimitResponse adminSetMarginLoanLimit(AdminSetMarginLoanLimitRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminSetMarginLoanLimit(request);
    }

    @Override
    public AdminQueryMarginLoanLimitResponse adminQueryMarginLoanLimit(AdminQueryMarginLoanLimitRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminQueryMarginLoanLimit(request);
    }

    @Override
    public AdminSetMarginUserLoanLimitResponse adminSetMarginUserLoanLimit(AdminSetMarginUserLoanLimitRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminSetMarginUserLoanLimit(request);
    }

    @Override
    public AdminQueryMarginUserLoanLimitResponse adminQueryMarginUserLoanLimit(AdminQueryMarginUserLoanLimitRequest request) {
        return grpcConfig.marginServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminQueryMarginUserLoanLimit(request);
    }
}
