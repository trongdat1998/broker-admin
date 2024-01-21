package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.service.MarginService;
import io.bhex.broker.admin.service.SymbolService;
import io.bhex.broker.grpc.margin.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-05-29 10:18
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/margin")
public class MarginController extends BrokerBaseController {

    @Autowired
    private MarginService marginService;

    @Autowired
    private SymbolService symbolService;

    /**
     * 设置币种配置
     *
     * @param po
     * @return
     */
    @RequestMapping(value = "/set/token_config", method = RequestMethod.POST)
    public ResultModel setTokenConfig(@RequestBody @Valid SetTokenConfigPO po) {
        SetTokenConfigResponse response = marginService.setTokenConfig(po, getOrgId());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("set token config failed!");
    }

    /**
     * 查询币种配置
     *
     * @param tokenId
     * @return
     */
    @RequestMapping(value = "/get/token_config", method = RequestMethod.GET)
    public ResultModel getTokenConfig(@RequestParam(name = "tokenId", required = false, defaultValue = "") String tokenId) {
        Long orgId = getOrgId();
        //log.info("MarginController getTokenConfig :orgId{}   tokenId:{}", orgId, tokenId);
        List<MarginTokenConfigDTO> vo = marginService.queryMarginTokenConfig(tokenId, orgId);
        return ResultModel.ok(vo);
    }

    /**
     * 设置风控
     *
     * @return
     */
    @RequestMapping(value = "/set/risk_config", method = RequestMethod.POST)
    public ResultModel setRiskConfig(@RequestBody @Valid SetRiskConfigPO po) {
        SetRiskConfigResponse response = marginService.setRiskConfig(getOrgId(), po.getWithdrawLine(), po.getWarnLine(), po.getAppendLine(), po.getStopLine(),
                po.getMaxLoanLimit(), po.getNotifyType(), po.getNotifyNumber(), po.getMaxLoanLimitVip1(), po.getMaxLoanLimitVip2(), po.getMaxLoanLimitVip3());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("set risk config failed!");
    }

    /**
     * 获取风控配置
     *
     * @return
     */
    @RequestMapping(value = {"/get/risk_config", "/risk/get/config"}, method = RequestMethod.GET)
    public ResultModel getRiskConfig() {
        List<RiskConfigDTO> vo = marginService.queryRiskConfig(getOrgId());
        return ResultModel.ok(vo);
    }

    /**
     * 设置币种利息表
     *
     * @return
     */
    @RequestMapping(value = "/set/interest_config", method = RequestMethod.POST)
    public ResultModel setInterestConfig(@RequestBody @Valid SetInterestConfigPO po) {
        SetInterestConfigResponse response = marginService.setInterestConfig(getOrgId(), po.getTokenId(), po.getInterest(), 1, 3600, 86400);
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("set interest config failed!");
    }

    /**
     * 查询币种利息配置
     *
     * @param tokenId
     * @return
     */
    @RequestMapping(value = "/get/interest_config", method = RequestMethod.GET)
    public ResultModel getInterestConfig(@RequestParam(name = "tokenId", required = false, defaultValue = "") String tokenId) {
        List<InterestConfigDTO> vo = marginService.queryInterestConfig(getOrgId(), tokenId);
        return ResultModel.ok(vo);
    }

    /**
     * 融币币对配置
     *
     * @return
     */
    @RequestMapping(value = "/set/margin_symbol", method = RequestMethod.POST)
    public ResultModel setMarginSymbol(@RequestBody @Valid SetMarginSymbolPO po) {
        SetMarginSymbolResponse response = marginService.setMarginSymbol(getOrgId(), po.getSymbolId(), po.getAllowMargin());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("set margin symbol failed!");
    }

    /**
     * 获取币池账户
     *
     * @return
     */
    @RequestMapping(value = {"/get/pool_account", "/risk/get/pool_account"}, method = RequestMethod.GET)
    public ResultModel getPoolAccount() {
        AccountDTO accountDTO = marginService.getPoolAccount(getOrgId());
        return ResultModel.ok(accountDTO);
    }

    /**
     * 查询借贷记录
     *
     * @param accountId
     * @param tokenId
     * @param loanId
     * @param status
     * @param fromLoanId
     * @param endLoanId
     * @param limit
     * @return
     */
    @RequestMapping(value = {"/risk/get/cross_loan_order", "/order/get/cross_loan_order", "/detail/get/cross_loan_order", "/rpt/margin_pool/cross_loan_order"}, method = RequestMethod.GET)
    public ResultModel getCrossLoanOrder(@RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                         @RequestParam(name = "accountId", required = false, defaultValue = "0") Long accountId,
                                         @RequestParam(name = "tokenId", required = false, defaultValue = "") String tokenId,
                                         @RequestParam(name = "loanId", required = false, defaultValue = "0") Long loanId,
                                         @RequestParam(name = "status", required = false, defaultValue = "0") Integer status,
                                         @RequestParam(name = "fromLoanId", required = false, defaultValue = "0") Long fromLoanId,
                                         @RequestParam(name = "endLoanId", required = false, defaultValue = "0") Long endLoanId,
                                         @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {
        GetCrossLoanOrderPO po = new GetCrossLoanOrderPO(userId, accountId, tokenId, loanId, status, fromLoanId, endLoanId, limit);
        List<CrossLoanOrderDTO> vo = marginService.queryCrossLoanOrder(getOrgId(), po);
        return ResultModel.ok(vo);
    }

    /**
     * 查询还币记录
     *
     * @param accountId
     * @param loanOrderId
     * @param tokenId
     * @param fromRepayId
     * @param endRepayId
     * @param limit
     * @return
     */
    @RequestMapping(value = {"/risk/get/repay_record", "/order/get/repay_record", "/detail/get/repay_record"}, method = RequestMethod.GET)
    public ResultModel getRepayRecord(@RequestParam(name = "accountId", required = false, defaultValue = "0") Long accountId,
                                      @RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                      @RequestParam(name = "loanOrderId", required = false, defaultValue = "0") Long loanOrderId,
                                      @RequestParam(name = "tokenId", required = false, defaultValue = "") String tokenId,
                                      @RequestParam(name = "fromRepayId", required = false, defaultValue = "0") Long fromRepayId,
                                      @RequestParam(name = "endRepayId", required = false, defaultValue = "0") Long endRepayId,
                                      @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {
        GetRepayRecordPO po = new GetRepayRecordPO(userId, accountId, loanOrderId, tokenId, fromRepayId, endRepayId, limit);
        List<RepayRecordDTO> vo = marginService.queryRepayRecord(getOrgId(), po);
        return ResultModel.ok(vo);
    }

    /**
     * 币池查询
     *
     * @return
     */
    @RequestMapping(value = "/coin_pool/query", method = RequestMethod.GET)
    public ResultModel queryCoinPool() {
        List<CoinPoolDTO> vo = marginService.queryCoinPool(getOrgId());
        return ResultModel.ok(vo);
    }

    /**
     * 用户风险度查询
     *
     * @return
     */
    @RequestMapping(value = "/risk/user_query", method = RequestMethod.GET)
    public ResultModel queryUserRisk() {
        List<UserRiskDTO> vo = marginService.queryUserRisk(getOrgId());
        return ResultModel.ok(vo);
    }

    /**
     * 用户风险度统计
     *
     * @return
     */
    @RequestMapping(value = "/risk/user_statistics", method = RequestMethod.GET)
    public ResultModel statisticsUserRisk() {
        UserRiskStatisticsDTO vo = marginService.sumUserRisk(getOrgId());
        return ResultModel.ok(vo);
    }

    /**
     * 用户强平
     *
     * @param po
     * @return
     */
    @RequestMapping(value = "/risk/close", method = RequestMethod.POST)
    public ResultModel forceClose(@RequestBody @Valid AccountInfoPO po) {
        ForceCloseResponse response = marginService.forceClose(getOrgId(), po.getAccountId(), getRequestUserId(), po.getDesc());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("force close failed!");
    }

    /**
     * 风险监控统计
     *
     * @return
     */
    @RequestMapping(value = "/risk/statistics", method = RequestMethod.GET)
    public ResultModel statisticsRisk() {
        RiskStatisticsDTO vo = marginService.sumAllRisk(getOrgId());
        return ResultModel.ok(vo);
    }

    /**
     * 每日风险监控统计
     *
     * @return
     */
    @RequestMapping(value = "/rpt/daily_statistics", method = RequestMethod.GET)
    public ResultModel queryRptDailyStatisticsRisk(
            @RequestParam(name = "toTime", required = false, defaultValue = "0") Long toTime,
            @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {
        List<DailyRiskStatisticsDTO> dtos = marginService.queryRptDailyStatisticsRisk(getOrgId(), toTime, limit);
        return ResultModel.ok(dtos);
    }

    /**
     * 用户持仓详情查询
     *
     * @param accountId
     * @return
     */
    @RequestMapping(value = {"/risk/user_position_detail/query", "/detail/user_position_detail/query"}, method = RequestMethod.GET)
    public ResultModel queryPositionDetail(@RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                           @RequestParam(name = "accountId", required = false, defaultValue = "0") Long accountId) {
        List<UserPositionDTO> vo = marginService.queryUserPosition(getOrgId(), accountId, userId);
        return ResultModel.ok(vo);
    }

    /**
     * 用户强平订单查询
     *
     * @param accountId
     * @param fromOrderId
     * @param endOrderId
     * @param limit
     * @return
     */
    @RequestMapping(value = {"/risk/force_close/query", "/detail/force_close/query"}, method = RequestMethod.GET)
    public ResultModel queryForceClose(@RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                       @RequestParam(name = "accountId", required = false, defaultValue = "0") Long accountId,
                                       @RequestParam(name = "fromOrderId", required = false, defaultValue = "0") Long fromOrderId,
                                       @RequestParam(name = "endOrderId", required = false, defaultValue = "0") Long endOrderId,
                                       @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {
        QueryForceClosePO po = new QueryForceClosePO(userId, accountId, fromOrderId, endOrderId, limit);
        List<OrderDTO> vo = marginService.queryForceClose(getOrgId(), po);
        return ResultModel.ok(vo);
    }

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/query/margin_symbol", method = RequestMethod.POST)
    public ResultModel querySymbol(@RequestBody @Valid QuerySymbolPO query) {
        long brokerId = getOrgId();

        PaginationVO<SymbolDTO> vo = symbolService.querySymbol(query.getCurrent(), query.getPageSize(),
                query.getCategory(), query.getQuoteToken(), query.getSymbolName(), brokerId, query.getExtraRequestInfos(), query.getCustomerQuoteId());
        return ResultModel.ok(vo);
    }

    /**
     * 查询强平执行记录
     *
     * @param accountId
     * @return
     */
    @RequestMapping(value = "/risk/query/force_recode", method = RequestMethod.GET)
    public ResultModel queryForceRecord(@RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                        @RequestParam(name = "accountId", required = false, defaultValue = "0") Long accountId,
                                        @RequestParam(name = "fromId", required = false, defaultValue = "0") Long fromId,
                                        @RequestParam(name = "toId", required = false, defaultValue = "0") Long toId,
                                        @RequestParam(name = "startTime", required = false, defaultValue = "0") Long startTime,
                                        @RequestParam(name = "endTime", required = false, defaultValue = "0") Long endTime,
                                        @RequestParam(name = "limit", required = false, defaultValue = "100") Integer limit) {
        List<ForceRecordDTO> vo = marginService.queryForceRecord(getOrgId(), accountId, userId, fromId, toId, startTime, endTime, limit);
        return ResultModel.ok(vo);
    }

    @RequestMapping(value = "/query/loan_limit/risk_config", method = RequestMethod.GET)
    public ResultModel queryAccountLoanLimit(@RequestParam(name = "vipLevel", required = false, defaultValue = "0") Integer vipLevel,
                                             @RequestParam(name = "userId", required = false, defaultValue = "0") Long userId) {
        List<AccountLoanLimitDTO> response = marginService.queryAccountLoanLimt(getOrgId(), userId, vipLevel);
        return ResultModel.ok(response);
    }

    @RequestMapping(value = "/set/loan_limit/risk_config", method = RequestMethod.POST)
    public ResultModel setAccountLoanLimit(@RequestBody @Valid SetAccountLoanLimitPO po) {
        SetAccountLoanLimitVIPResponse response = marginService.setAccountLoanLimit(getOrgId(), po.getVipLevel(), po.getUserIds());
        if (response.getRet() != 0) {
            if (response.getRet() == 31018) {
                return ResultModel.error("no loan account in the user");
            }
            return ResultModel.error("set account loan limit level error!");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/delete/loan_limit/risk_config", method = RequestMethod.POST)
    public ResultModel deleteAccountLoanLimit(@RequestBody @Valid DeleteAccountLoanLimitPO po) {
        DeleteAccountLoanLimitVIPResponse response = marginService.deleteAccountLoanLimit(getOrgId(), po.getUserId());
        if (response.getRet() != 0) {
            return ResultModel.error("delete account loan limit level error!");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/rpt/margin_pool", method = RequestMethod.GET)
    public ResultModel queryMarginPoolRpt(@RequestParam(name = "tokenId", required = false, defaultValue = "") String tokenId) {
        List<MarginPoolRptDTO> result = marginService.queryMarginPoolRpt(getOrgId(), tokenId);
        return ResultModel.ok(result);
    }

    /**
     * 查询杠杆风控黑名单
     *
     * @param userId
     * @param confGroup 配置 margin.risk.calculation：计算黑名单
     * @return
     */
    @RequestMapping(value = "/query/risk_config/black_list", method = RequestMethod.GET)
    public ResultModel queryMarginRiskBlack(@RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                            @RequestParam(name = "confGroup", required = false, defaultValue = "margin.risk.calculation") String confGroup) {
        List<MarginRiskBlackListDTO> result = marginService.queryMarginRiskBlackList(getOrgId(), userId, confGroup);
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/add/risk_config/black_list", method = RequestMethod.POST)
    public ResultModel addMarginRiskBlack(@RequestBody @Valid MarginRiskBlackPO po) {
        AdminUserReply adminUser = getRequestUser();
        AddMarginRiskBlackListResponse response = marginService.addMarginRiskBlackList(adminUser.getOrgId(), po.getUserId(), po.getConfGroup(), adminUser.getUsername(), po.getReason());
        if (response.getRet() != 0) {
            return ResultModel.error("add margin risk black error!");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/delete/risk_config/black_list", method = RequestMethod.POST)
    public ResultModel deleteMarginRiskBlack(@RequestBody @Valid MarginRiskBlackPO po) {
        DelMarginRiskBlackListResponse response = marginService.delMarginRiskBlackList(getOrgId(), po.getUserId(), po.getConfGroup());
        if (response.getRet() != 0) {
            return ResultModel.error("delete margin risk black error!");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/rpt/margin_trade", method = RequestMethod.GET)
    public ResultModel queryRptMarginTrade(@RequestParam(name = "toTime", required = false, defaultValue = "0") Long toTime,
                                           @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {
        List<RptMarginTradeDTO> result = marginService.queryRptMarginTrade(getOrgId(), toTime, limit);
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/rpt/margin_trade_detail", method = RequestMethod.GET)
    public ResultModel queryRptMarginTradeDetail(@RequestParam(name = "relationId", required = false, defaultValue = "0") Long relationId) {
        List<RptMarginTradeDetailDTO> result = marginService.queryRptMarginTradeDetail(getOrgId(), relationId);
        return ResultModel.ok(result);
    }

    /**
     * 设置币种特殊利息表
     *
     * @return
     */
    @RequestMapping(value = "/set/special/interest_config", method = RequestMethod.POST)
    public ResultModel setSpecialInterestConfig(@RequestBody @Valid SetSpecialInterestPo po) {
        SetSpecialInterestResponse response = marginService.setSpecialInterestConfig(getOrgId(), po.getTokenId(), po.getInterest(), po.getUserId(), po.getEffectiveFlag());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("set special interest config failed!");
    }

    /**
     * 查询币种特殊利息表
     *
     * @param tokenId
     * @return
     */
    @RequestMapping(value = "/get/special/interest_config", method = RequestMethod.GET)
    public ResultModel getSpecialInterestConfig(@RequestParam(name = "tokenId", required = false, defaultValue = "") String tokenId,
                                                @RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                                @RequestParam(name = "accountId", required = false, defaultValue = "0") Long accountId) {
        List<SpecialInterestDTO> vo = marginService.querySpecialInterestConfig(getOrgId(), tokenId, userId, accountId);
        return ResultModel.ok(vo);
    }

    /**
     * 设置币种特殊利息表
     *
     * @return
     */
    @RequestMapping(value = "/del/special/interest_config", method = RequestMethod.POST)
    public ResultModel delSpecialInterestConfig(@RequestBody @Valid DelSpecialInterestPo po) {
        DeleteSpecialInterestResponse response = marginService.delSpecialInterestConfig(getOrgId(), po.getTokenId(), po.getUserId());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("del special interest config failed!");
    }

    /**
     * 查询开户活动
     * @param userId
     * @param accountId
     * @param startTime
     * @param endTime
     * @param fromId
     * @param joinStatus
     * @param limit
     * @return
     */
    @RequestMapping(value = "/rpt/open_margin")
    public ResultModel queryOpenMarginActivity(@RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                               @RequestParam(name = "accountId", required = false, defaultValue = "0") Long accountId,
                                               @RequestParam(name = "startTime", required = false, defaultValue = "0") Long startTime,
                                               @RequestParam(name = "endTime", required = false, defaultValue = "0") Long endTime,
                                               @RequestParam(name = "fromId", required = false, defaultValue = "0") Long fromId,
                                               @RequestParam(name = "joinStatus", required = false, defaultValue = "0") Integer joinStatus,
                                               @RequestParam(name = "limit", required = false, defaultValue = "500") Integer limit) {
        List<OpenMarginActivityDTO> result = marginService.queryOpenMarginActivity(getOrgId(), userId, accountId, startTime, endTime, fromId, joinStatus, limit);
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/rpt/check/open_margin_day", method = RequestMethod.POST)
    public ResultModel checkOpenMarginDay() {
        AdminCheckDayOpenMarginActivityResponse response = marginService.adminCheckDayOpenMarginActivity(getOrgId());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("check day open margin activity failed!");
    }

    @RequestMapping(value = "/rpt/check/open_margin_month", method = RequestMethod.POST)
    public ResultModel checkOpenMarginMonth() {
        AdminCheckMonthOpenMarginActivityResponse response = marginService.adminCheckMonthOpenMarginActivity(getOrgId());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("check month open margin activity failed!");
    }

    @RequestMapping(value = "/rpt/get/profit_activtiy")
    public ResultModel getProfitActivity(@RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                         @RequestParam(name = "accountId", required = false, defaultValue = "0") Long accountId,
                                         @RequestParam(name = "beginDate", required = false, defaultValue = "0") Long beginDate,
                                         @RequestParam(name = "endDate", required = false, defaultValue = "0") Long endDate,
                                         @RequestParam(name = "fromId", required = false, defaultValue = "0") Long fromId,
                                         @RequestParam(name = "joinStatus", required = false, defaultValue = "0") Integer joinStatus,
                                         @RequestParam(name = "limit", required = false, defaultValue = "500") Integer limit) {
        List<MarginProfitDTO> result = marginService.queryMarginProfitActivity(getOrgId(), userId, beginDate, endDate, joinStatus, fromId, accountId, limit);
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/rpt/sort_profit", method = RequestMethod.POST)
    public ResultModel sortProfitActivity() {
        AdminSortProfitRankingResponse response = marginService.adminSortProfitRanking(getOrgId());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("sort profit margin activity failed!");
    }

    @RequestMapping(value = "/rpt/set_profit_ranking", method = RequestMethod.POST)
    public ResultModel setProfitRanking(@RequestBody @Valid SetMarginProfitRankingPO po) {
        AdminSetProfitRankingResponse response = marginService.adminSetProfitRanking(getOrgId(), po.getJoinDate(), po.getUserId(), po.getRanking());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("set profit margin activity failed!");
    }

    @RequestMapping(value = "/rpt/recal_profit_top", method = RequestMethod.POST)
    public ResultModel recalProfitTop(@RequestBody @Valid MarginRecalTopProfitPO po) {
        AdminRecalTopProfitRateResponse response = marginService.adminRecalTopProfitRate(getOrgId(), po.getJoinDate(), po.getTop());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("recal profit margin activity failed!");
    }

    /**
     * 设置特殊借币限额 -- 新系统可暂不支持
     *
     * @param po
     * @return
     */
    @RequestMapping(value = "/set/risk_config/special_loan_limit", method = RequestMethod.POST)
    public ResultModel setSpecialLoanLimit(@RequestBody @Valid SetSpecialLoanLimitPO po) {
        AdminSetSpecialLoanLimitResponse response = marginService.adminSetSpecialLoanLimit(getOrgId(), po.getUserId(), po.getLoanLimit(), po.getIsOpen());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("set special loan limit failed!");
    }

    /**
     * 查询特殊借币限额 -- 新系统可暂不支持
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/query/risk_config/special_loan_limit")
    public ResultModel querySpecialLoanLimit(@RequestParam(name = "userId", required = false, defaultValue = "0") Long userId) {
        List<SpecialLoanLimitDTO> result = marginService.querySpecialLoanLimit(getOrgId(), userId);
        return ResultModel.ok(result);
    }

    /**
     * 删除特殊借币限额 -- 新系统可暂不支持
     */
    @RequestMapping(value = "/del/risk_config/special_loan_limit" , method = RequestMethod.POST)
    public ResultModel delSpecialLoanLimit(@RequestBody @Valid DelSpecialLoanLimitPO po) {
        AdminDelSpecialLoanLimitResponse response = marginService.adminDelSpecialLoanLimit(getOrgId(), po.getUserId());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("del special loan limit failed!");
    }

    /**
     * 设置币种借贷数量上限
     *
     * @return
     */
    @RequestMapping(value = "/set/token_config/loan_limit", method = RequestMethod.POST)
    public ResultModel setMarginLoanLimit(@RequestBody @Valid SetMarginLoanLimitPo po) {

        Long orgId = getOrgId();

        AdminSetMarginLoanLimitResponse response = marginService.adminSetMarginLoanLimit(orgId, po.getTokenId(), po.getLimitAmount(), po.getStatus());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("set margin loan limit failed!");
    }

    /**
     * 查询币种借贷数量上限
     *
     * @param tokenId
     * @return
     */
    @RequestMapping(value = "/get/token_config/loan_limit", method = RequestMethod.GET)
    public ResultModel queryMarginLoanLimit(@RequestParam(name = "tokenId", required = false, defaultValue = "") String tokenId) {

        List<MarginLoanLimitDTO> dtos = marginService.adminQueryMarginLoanLimit(getOrgId(), tokenId);
        return ResultModel.ok(dtos);
    }

    /**
     * 设置币种用户借贷数量上限
     *
     * @return
     */
    @RequestMapping(value = "/set/token_config/user_loan_limit", method = RequestMethod.POST)
    public ResultModel setMarginUserLoanLimit(@RequestBody @Valid SetMarginUserLoanLimitPo po) {
        Long orgId = getOrgId();

        AdminSetMarginUserLoanLimitResponse response = marginService.adminSetMarginUserLoanLimit(orgId, po.getUserId(), po.getTokenId(), po.getLimitAmount(), po.getStatus());
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("set margin loan limit failed!");
    }

    /**
     * 查询币种用户借贷数量上限
     *
     * @param tokenId
     * @return
     */
    @RequestMapping(value = "/get/token_config/user_loan_limit", method = RequestMethod.GET)
    public ResultModel queryMarginUserLoanLimit(@RequestParam(name = "tokenId", required = false, defaultValue = "") String tokenId,
                                                @RequestParam(name = "userId", required = false, defaultValue = "0") Long userId,
                                                @RequestParam(name = "accountId", required = false, defaultValue = "0") Long accountId) {

        List<MarginUserLoanLimitDTO> dtos = marginService.adminQueryMarginUserLoanLimit(getOrgId(), tokenId, userId, accountId);
        return ResultModel.ok(dtos);
    }
}
