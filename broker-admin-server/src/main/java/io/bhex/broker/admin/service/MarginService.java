package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.GetCrossLoanOrderPO;
import io.bhex.broker.admin.controller.param.GetRepayRecordPO;
import io.bhex.broker.admin.controller.param.QueryForceClosePO;
import io.bhex.broker.admin.controller.param.SetTokenConfigPO;
import io.bhex.broker.grpc.margin.*;

import java.util.List;

public interface MarginService {

    SetTokenConfigResponse setTokenConfig(SetTokenConfigPO po, Long orgId);

    List<MarginTokenConfigDTO> queryMarginTokenConfig(String tokenId, Long orgId);

    SetRiskConfigResponse setRiskConfig(Long orgId, String withdrawLine, String warnLine, String appendLine, String stopLine
            , String maxLoanLimit, Integer notifyType, String notifyNumber, String maxLoanLimitVip1, String maxLoanLimitVip2, String maxLoanLimitVip3);

    List<RiskConfigDTO> queryRiskConfig(Long orgId);

    SetInterestConfigResponse setInterestConfig(Long orgId, String tokenId, String interest, int interestPeriod, int calculationPeriod, int settlementPeriod);

    List<InterestConfigDTO> queryInterestConfig(Long orgId, String tokenId);

    SetMarginSymbolResponse setMarginSymbol(Long orgId, String symbolId, boolean allowMargin);

    AccountDTO getPoolAccount(Long orgId);

    List<CrossLoanOrderDTO> queryCrossLoanOrder(Long orgId, GetCrossLoanOrderPO po);

    List<RepayRecordDTO> queryRepayRecord(Long orgId, GetRepayRecordPO po);

    List<CoinPoolDTO> queryCoinPool(Long orgId);

    List<UserRiskDTO> queryUserRisk(Long orgId);

    UserRiskStatisticsDTO sumUserRisk(Long orgId);

    ForceCloseResponse forceClose(Long orgId, Long accountId, Long adminUserId, String desc);

    RiskStatisticsDTO sumAllRisk(Long orgId);

    List<DailyRiskStatisticsDTO> queryRptDailyStatisticsRisk(Long orgId, Long toTime, Integer limit);

    List<UserPositionDTO> queryUserPosition(Long orgId, Long accountId, Long userId);

    List<OrderDTO> queryForceClose(Long orgId, QueryForceClosePO po);

    List<InterestConfigDTO> queryMarginInterestByLevel(Long orgId, Long levelConfigId);

    List<ForceRecordDTO> queryForceRecord(Long orgId, Long accoutId, Long userId, Long fromId, Long toId, Long startTime, Long endTime, Integer limit);

    MarginPositionStatusDTO queryMarginPositionStatus(Long orgId, Long userId);

    AdminChangeMarginPositionStatusResponse changeMarginPositionStatus(Long orgId, Long userId, Integer changeToStatus, Integer curStatus);

    List<AccountLoanLimitDTO> queryAccountLoanLimt(Long orgId, Long userId, Integer vipLevel);

    SetAccountLoanLimitVIPResponse setAccountLoanLimit(Long orgId, Integer vipLevel, String userIds);

    DeleteAccountLoanLimitVIPResponse deleteAccountLoanLimit(Long orgId, Long userId);

    List<MarginPoolRptDTO> queryMarginPoolRpt(Long orgId, String tokenId);

    List<MarginRiskBlackListDTO> queryMarginRiskBlackList(Long orgId, Long userId, String confGroup);

    AddMarginRiskBlackListResponse addMarginRiskBlackList(Long orgId, Long userId, String confGroup, String adminUserId, String reason);

    DelMarginRiskBlackListResponse delMarginRiskBlackList(Long orgId, Long userId, String confGroup);

    List<RptMarginTradeDTO> queryRptMarginTrade(Long orgId, Long toTime, Integer limit);

    List<RptMarginTradeDetailDTO> queryRptMarginTradeDetail(Long orgId, Long relationId);

    SetSpecialInterestResponse setSpecialInterestConfig(Long orgId, String tokenId, String interest, Long userId, Integer effectiveFlag);

    List<SpecialInterestDTO> querySpecialInterestConfig(Long orgId, String tokenId, Long userId, Long accountId);

    DeleteSpecialInterestResponse delSpecialInterestConfig(Long orgId, String tokenId, Long userId);

    List<OpenMarginActivityDTO> queryOpenMarginActivity(Long orgId, Long userId, Long accountId, Long startTime, Long endTime, Long fromId, Integer joinStatus, Integer limit);

    AdminCheckDayOpenMarginActivityResponse adminCheckDayOpenMarginActivity(Long orgId);

    AdminCheckMonthOpenMarginActivityResponse adminCheckMonthOpenMarginActivity(Long orgId);

    List<MarginProfitDTO> queryMarginProfitActivity(Long orgId, Long userId, Long beginDate, Long endDate, Integer joinStatus, Long fromId, Long accountId, Integer limit);

    AdminSortProfitRankingResponse adminSortProfitRanking(Long orgId);

    AdminSetProfitRankingResponse adminSetProfitRanking(Long orgId, Long joinDate, Long userId, Integer ranking);

    AdminRecalTopProfitRateResponse adminRecalTopProfitRate(Long orgId, Long joinDate, Integer top);


    AdminSetSpecialLoanLimitResponse adminSetSpecialLoanLimit(Long orgId, Long userId, String loanLimit, Integer isOpen);

    List<SpecialLoanLimitDTO> querySpecialLoanLimit(Long orgId,Long userId);

    AdminDelSpecialLoanLimitResponse adminDelSpecialLoanLimit(Long orgId,Long userId);

    AdminSetMarginLoanLimitResponse adminSetMarginLoanLimit(Long orgId, String tokenId, String limitAmount, Integer status);

    List<MarginLoanLimitDTO> adminQueryMarginLoanLimit(Long orgId, String tokenId);

    AdminSetMarginUserLoanLimitResponse adminSetMarginUserLoanLimit(Long orgId, Long userId, String tokenId, String limitAmount, Integer status);

    List<MarginUserLoanLimitDTO> adminQueryMarginUserLoanLimit(Long orgId, String tokenId, Long userId, Long accountId);

}
