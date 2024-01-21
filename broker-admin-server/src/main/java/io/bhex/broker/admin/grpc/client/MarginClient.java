package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.margin.*;

public interface MarginClient {

    SetTokenConfigResponse setTokenConfig(SetTokenConfigRequest request);

    GetTokenConfigResponse queryMarginTokenConfig(String tokenId, Long orgId);

    SetRiskConfigResponse setRiskConfig(SetRiskConfigRequest request);

    GetRiskConfigResponse queryRiskConfig(Long orgId);

    SetInterestConfigResponse setInterestConfig(SetInterestConfigRequest request);

    GetInterestConfigResponse queryInterestConfig(String tokenId, Long orgId);

    SetMarginSymbolResponse setMarginSymbol(SetMarginSymbolRequest request);

    GetPoolAccountResponse getPoolAccount(Long orgId);

    GetCrossLoanOrderResponse queryCrossLoanOrder(GetCrossLoanOrderRequest request);

    GetRepayRecordResponse queryRepayRecord(GetRepayRecordRequest request);

    QueryCoinPoolResponse queryCoinPool(QueryCoinPoolRequest request);

    QueryUserRiskResponse queryUserRisk(QueryUserRiskRequest request);

    StatisticsUserRiskResponse statisticsUserRisk(StatisticsUserRiskRequest request);

    ForceCloseResponse forceClose(ForceCloseRequest request);

    StatisticsRiskResponse statisticsRisk(StatisticsRiskRequest request);

    QueryRptDailyStatisticsRiskResponse queryRptDailyStatisticsRisk(QueryRptDailyStatisticsRiskRequest request);

    QueryUserPositionResponse queryPositionDetail(QueryUserPositionRequest request);

    QueryForceCloseResponse queryForceClose(QueryForceCloseRequest request);

    QueryInterestByLevelResponse queryInteresyBtLevel(QueryInterestByLevelRequest request);

    AdminQueryForceRecordResponse adminQueryForceRecord(AdminQueryForceRecordRequest request);

    AdminQueryAccountStatusResponse adminQueryAccountStatus(AdminQueryAccountStatusRequest request);

    AdminChangeMarginPositionStatusResponse adminChangeMarginPosition(AdminChangeMarginPositionStatusRequest request);

    QueryAccountLoanLimitVIPResponse queryAccountLoanLimitVIP(QueryAccountLoanLimitVIPRequest request);

    SetAccountLoanLimitVIPResponse setAccountLoanLimitVIP(SetAccountLoanLimitVIPRequest request);

    DeleteAccountLoanLimitVIPResponse deleteAccountLoanLimitVIP(DeleteAccountLoanLimitVIPRequest request);

    QueryRptMarginPoolResponse queryRptMarginPool(QueryRptMarginPoolRequest request);

    QueryMarginRiskBlackListResponse queryMarginRiskBlackList(QueryMarginRiskBlackListRequest request);

    AddMarginRiskBlackListResponse addMarginRiskBlackList(AddMarginRiskBlackListRequest request);

    DelMarginRiskBlackListResponse delMarginRiskBlackList(DelMarginRiskBlackListRequest request);

    QueryRptMarginTradeResponse queryRptMarginTrade(QueryRptMarginTradeRequest request);

    QueryRptMarginTradeDetailResponse queryRptMarginTradeDetail(QueryRptMarginTradeDetailRequest request);

    SetSpecialInterestResponse setSpecialInterest(SetSpecialInterestRequest request);

    QuerySpecialInterestResponse querySpecialInterest(QuerySpecialInterestRequest request);

    DeleteSpecialInterestResponse deleteSpecialInterest(DeleteSpecialInterestRequest request);

    AdminQueryOpenMarginActivityResponse queryOpenMarginActivity(AdminQueryOpenMarginActivityRequest request);

    AdminCheckDayOpenMarginActivityResponse adminCheckDayOpenMarginActivity(AdminCheckDayOpenMarginActivityRequest request);

    AdminCheckMonthOpenMarginActivityResponse adminCheckMonthOpenMarginActivity(AdminCheckMonthOpenMarginActivityRequest request);

    AdminQueryProfitActivityResponse adminQueryProfitActivity(AdminQueryProfitActivityRequest request);

    AdminSetProfitRankingResponse adminSetProfitRanking(AdminSetProfitRankingRequest request);

    AdminSortProfitRankingResponse adminSortProfitRanking(AdminSortProfitRankingRequest request);

    AdminRecalTopProfitRateResponse adminRecalTopProfitRate(AdminRecalTopProfitRateRequst request);

    AdminSetSpecialLoanLimitResponse adminSetSpecialLoanLimit(AdminSetSpecialLoanLimitRequest request);

    AdminQuerySpecialLoanLimitResponse adminQuerySpecialLoanLimit(AdminQuerySpecialLoanLimitRequest request);

    AdminDelSpecialLoanLimitResponse adminDelSpecialLoanLimit(AdminDelSpecialLoanLimitRequest request);

    AdminSetMarginLoanLimitResponse adminSetMarginLoanLimit(AdminSetMarginLoanLimitRequest request);

    AdminQueryMarginLoanLimitResponse adminQueryMarginLoanLimit(AdminQueryMarginLoanLimitRequest request);

    AdminSetMarginUserLoanLimitResponse adminSetMarginUserLoanLimit(AdminSetMarginUserLoanLimitRequest request);

    AdminQueryMarginUserLoanLimitResponse adminQueryMarginUserLoanLimit(AdminQueryMarginUserLoanLimitRequest request);

}
