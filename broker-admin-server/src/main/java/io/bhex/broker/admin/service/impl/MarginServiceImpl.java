package io.bhex.broker.admin.service.impl;

import com.google.common.collect.Lists;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.GetCrossLoanOrderPO;
import io.bhex.broker.admin.controller.param.GetRepayRecordPO;
import io.bhex.broker.admin.controller.param.QueryForceClosePO;
import io.bhex.broker.admin.controller.param.SetTokenConfigPO;
import io.bhex.broker.admin.grpc.client.MarginClient;
import io.bhex.broker.admin.service.MarginService;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.margin.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MarginServiceImpl implements MarginService {

    @Resource
    MarginClient marginClient;

    @Override
    public SetTokenConfigResponse setTokenConfig(SetTokenConfigPO po, Long orgId) {
        SetTokenConfigRequest request = SetTokenConfigRequest.newBuilder()
                .setTokenId(po.getTokenId())
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setConvertRate(po.getConvertRate())
                .setLeverage(Integer.parseInt(po.getLeverage()))
                .setCanBorrow(po.getCanBorrow())
                .setMaxQuantity(po.getMaxQuantity())
                .setMinQuantity(po.getMinQuantity())
                .setQuantityPrecision(po.getQuantityPrecision())
                .setRepayMinQuantity(po.getRepayMinQuantity())
                .setIsOpen(po.getIsOpen())
                .setShowInterestPeriod(po.getShowInterestPrecision())
                .build();
        return marginClient.setTokenConfig(request);
    }

    @Override
    public List<MarginTokenConfigDTO> queryMarginTokenConfig(String tokenId, Long orgId) {

        GetTokenConfigResponse response = marginClient.queryMarginTokenConfig(tokenId, orgId);

        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getTokenConfigList())) {
            return Lists.newArrayList();
        }

        return response.getTokenConfigList().stream().map(
                i -> {
                    return MarginTokenConfigDTO.builder()
                            .orgId(i.getOrgId())
                            .exchangeId(i.getExchangeId())
                            .tokenId(i.getTokenId())
                            .convertRate(i.getConvertRate())
                            .leverage((long) i.getLeverage())
                            .canBorrow(i.getCanBorrow())
                            .maxQuantity(i.getMaxQuantity())
                            .minQuantity(i.getMinQuantity())
                            .quantityPrecision((long) i.getQuantityPrecision())
                            .repayMinQuantity(i.getRepayMinQuantity())
                            .created(i.getCreated())
                            .updated(i.getUpdated())
                            .isOpen(i.getIsOpen())
                            .showInterestPrecision(i.getShowInterestPeriod())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    public SetRiskConfigResponse setRiskConfig(Long orgId, String withdrawLine, String warnLine, String appendLine, String stopLine,
                                               String maxLoanLimit, Integer notifyType, String notifyNumber, String maxLoanLimitVip1, String maxLoanLimitVip2, String maxLoanLimitVip3) {
        BigDecimal hundred = new BigDecimal("100");
        SetRiskConfigRequest request = SetRiskConfigRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setWithdrawLine(DecimalUtil.toTrimString(new BigDecimal(withdrawLine).divide(hundred, 4, BigDecimal.ROUND_DOWN)))
                .setWarnLine(DecimalUtil.toTrimString(new BigDecimal(warnLine).divide(hundred, 4, BigDecimal.ROUND_DOWN)))
                .setAppendLine(DecimalUtil.toTrimString(new BigDecimal(appendLine).divide(hundred, 4, BigDecimal.ROUND_DOWN)))
                .setStopLine(DecimalUtil.toTrimString(new BigDecimal(stopLine).divide(hundred, 4, BigDecimal.ROUND_DOWN)))
                .setMaxLoanLimiit(DecimalUtil.toTrimString(new BigDecimal(maxLoanLimit)))
                .setNotifyType(notifyType)
                .setNotifyNumber(notifyNumber)
                .setMaxLoanLimitVip1(DecimalUtil.toTrimString(new BigDecimal(maxLoanLimitVip1)))
                .setMaxLoanLimitVip2(DecimalUtil.toTrimString(new BigDecimal(maxLoanLimitVip2)))
                .setMaxLoanLimitVip3(DecimalUtil.toTrimString(new BigDecimal(maxLoanLimitVip3)))
                .build();
        return marginClient.setRiskConfig(request);
    }

    @Override
    public List<RiskConfigDTO> queryRiskConfig(Long orgId) {
        GetRiskConfigResponse response = marginClient.queryRiskConfig(orgId);
        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getRiskConfigList())) {
            return Lists.newArrayList();
        }
        BigDecimal hundred = new BigDecimal("100");
        return response.getRiskConfigList().stream().map(
                i -> RiskConfigDTO.builder()
                        .id(i.getId())
                        .orgId(i.getOrgId())
                        .withdrawLine(DecimalUtil.toTrimString(new BigDecimal(i.getWithdrawLine()).multiply(hundred)))
                        .warnLine(DecimalUtil.toTrimString(new BigDecimal(i.getWarnLine()).multiply(hundred)))
                        .appendLine(DecimalUtil.toTrimString(new BigDecimal(i.getAppendLine()).multiply(hundred)))
                        .stopLine(DecimalUtil.toTrimString(new BigDecimal(i.getStopLine()).multiply(hundred)))
                        .maxLoanLimit(new BigDecimal(i.getMaxLoanLimiit()).stripTrailingZeros().toPlainString())
                        .notifyType(i.getNotifyType())
                        .notifyNumber(i.getNotifyNumber())
                        .maxLoanLimitVip1(new BigDecimal(i.getMaxLoanLimitVip1()).stripTrailingZeros().toPlainString())
                        .maxLoanLimitVip2(new BigDecimal(i.getMaxLoanLimitVip2()).stripTrailingZeros().toPlainString())
                        .maxLoanLimitVip3(new BigDecimal(i.getMaxLoanLimitVip3()).stripTrailingZeros().toPlainString())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public SetInterestConfigResponse setInterestConfig(Long orgId, String tokenId, String interest, int interestPeriod, int calculationPeriod, int settlementPeriod) {
        BigDecimal hundred = new BigDecimal("100");
        SetInterestConfigRequest request = SetInterestConfigRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTokenId(tokenId)
                .setInterest(DecimalUtil.toTrimString(new BigDecimal(interest).divide(hundred, 8, BigDecimal.ROUND_DOWN)))
                .setInterestPeriod(1)
                .setCalculationPeriod(3600)
                .setSettlementPeriod(86400)
                .build();
        return marginClient.setInterestConfig(request);
    }

    @Override
    public List<InterestConfigDTO> queryInterestConfig(Long orgId, String tokenId) {
        GetInterestConfigResponse response = marginClient.queryInterestConfig(tokenId, orgId);
        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getInterestConfigList())) {
            return Lists.newArrayList();
        }

        return response.getInterestConfigList().stream().map(this::getInterestConfigDto).collect(Collectors.toList());

    }

    private InterestConfigDTO getInterestConfigDto(InterestConfig config) {
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal freeInterest = new BigDecimal("-1");
        return InterestConfigDTO.builder()
                .id(config.getId())
                .orgId(config.getOrgId())
                .tokenId(config.getTokenId())
                .interest(new BigDecimal(config.getInterest()).compareTo(freeInterest) <= 0 ? new BigDecimal(config.getInterest()).stripTrailingZeros().toPlainString() :
                        new BigDecimal(config.getInterest()).multiply(hundred).multiply(new BigDecimal("86400")).stripTrailingZeros().toPlainString())
                .interestPeriod(Long.valueOf(config.getInterestPeriod()))
                .calculationPeriod(Long.valueOf(config.getCalculationPeriod()))
                .settlementPeriod(Long.valueOf(config.getSettlementPeriod()))
                .showInterest(new BigDecimal(config.getInterest()).compareTo(freeInterest) <= 0 ? new BigDecimal(config.getShowInterest()).stripTrailingZeros().toPlainString() :
                        new BigDecimal(config.getShowInterest()).multiply(hundred).stripTrailingZeros().toPlainString())
                .levelConfigId(config.getLevelConfigId())
                .created(config.getCreated())
                .updated(config.getUpdated())
                .build();
    }

    @Override
    public SetMarginSymbolResponse setMarginSymbol(Long orgId, String symbolId, boolean allowMargin) {
        SetMarginSymbolRequest request = SetMarginSymbolRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setSymbolId(symbolId)
                .setAllowMargin(allowMargin)
                .build();
        return marginClient.setMarginSymbol(request);
    }

    @Override
    public AccountDTO getPoolAccount(Long orgId) {
        GetPoolAccountResponse response = marginClient.getPoolAccount(orgId);
        List<AccountTokenInfo> list = response.getTokenList().stream().map(i -> {
            return AccountTokenInfo.builder()
                    .tokenId(i.getTokenId())
                    .borrowed(i.getBorrowed())
                    .loanable(i.getLoanable())
                    .build();
        }).collect(Collectors.toList());

        return AccountDTO.builder()
                .id(response.getId())
                .orgId(response.getOrgId())
                .accountId(response.getAccountId())
                .accountName(response.getAccountName())
                .accountType(Long.valueOf(response.getAccountType()))
                .accountIndex(Long.valueOf(response.getAccountIndex()))
                .authorizedOrg(response.getAuthorizedOrg())
                .tokenList(list)
                .build();
    }

    @Override
    public List<CrossLoanOrderDTO> queryCrossLoanOrder(Long orgId, GetCrossLoanOrderPO po) {
        /*if (po.getAccountId() == 0 && po.getUserId() == 0) {
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER, "accountId and userId is 0");
        }*/
        GetCrossLoanOrderRequest request = GetCrossLoanOrderRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(po.getUserId()).build())
                .setAccountId(po.getAccountId())
                .setTokenId(StringUtils.isEmpty(po.getTokenId()) ? "" : po.getTokenId())
                .setLoanId(po.getLoanId())
                .setStatus(po.getStatus())
                .setFromLoanId(po.getFromLoanId())
                .setEndLoanId(po.getEndLoanId())
                .setLimit(po.getLimit())
                .build();
        GetCrossLoanOrderResponse response = marginClient.queryCrossLoanOrder(request);

        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getCrossLoanOrderList())) {
            return Lists.newArrayList();
        }

        return response.getCrossLoanOrderList().stream().map(
                i -> {
                    return CrossLoanOrderDTO.builder()
                            .loanOrderId(i.getLoanOrderId())
                            .clientId(i.getClientId())
                            .orgId(i.getOrgId())
                            .balanceId(i.getBalanceId())
                            .lenderAccountId(i.getLenderAccountId())
                            .lenderId(i.getLenderId())
                            .tokenId(i.getTokenId())
                            .loanAmount(i.getLoanAmount())
                            .repaidAmount(i.getRepaidAmount())
                            .unpaidAmount(i.getUnpaidAmount())
                            .interestRate1(i.getInterestRate1())
                            .interestStart(i.getInterestStart())
                            .status(i.getStatus())
                            .interestPaid(i.getInterestPaid())
                            .interestUnpaid(i.getInterestUnpaid())
                            .createdAt(i.getCreatedAt())
                            .updatedAt(i.getUpdatedAt())
                            .accountId(i.getAccountId())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<RepayRecordDTO> queryRepayRecord(Long orgId, GetRepayRecordPO po) {
        if (po.getAccountId() == 0 && po.getUserId() == 0) {
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER, "accountId and userId is 0");
        }
        GetRepayRecordRequest request = GetRepayRecordRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(po.getUserId()).build())
                .setAccountId(po.getAccountId())
                .setLoanOrderId(po.getLoanOrderId())
                .setTokenId(StringUtils.isEmpty(po.getTokenId()) ? "" : po.getTokenId())
                .setFromRepayId(po.getFromRepayId())
                .setEndRepayId(po.getEndRepayId())
                .setLimit(po.getLimit())
                .build();
        GetRepayRecordResponse response = marginClient.queryRepayRecord(request);

        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getRepayRecordList())) {
            return Lists.newArrayList();
        }

        return response.getRepayRecordList().stream().map(
                i -> {
                    return RepayRecordDTO.builder()
                            .repayOrderId(i.getRepayOrderId())
                            .orgId(i.getOrgId())
                            .accountId(i.getAccountId())
                            .clientId(i.getClientId())
                            .balanceId(i.getBalanceId())
                            .tokenId(i.getTokenId())
                            .loanOrderId(i.getLoanOrderId())
                            .amount(i.getAmount())
                            .interest(i.getInterest())
                            .createdAt(i.getCreatedAt())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<CoinPoolDTO> queryCoinPool(Long orgId) {
        QueryCoinPoolRequest request = QueryCoinPoolRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        QueryCoinPoolResponse response = marginClient.queryCoinPool(request);

        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getCoinPoolList())) {
            return Lists.newArrayList();
        }

        return response.getCoinPoolList().stream().map(
                i -> {
                    return CoinPoolDTO.builder()
                            .tokenId(i.getTokenId())
                            .total(i.getTotal())
                            .borrowed(i.getBorrowed())
                            .rate(i.getRate())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<UserRiskDTO> queryUserRisk(Long orgId) {
        QueryUserRiskRequest request = QueryUserRiskRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        QueryUserRiskResponse response = marginClient.queryUserRisk(request);

        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getUserRiskList())) {
            return Lists.newArrayList();
        }

        return response.getUserRiskList().stream().map(
                i -> {
                    return UserRiskDTO.builder()
                            .accountId(i.getAccountId())
                            .total(i.getTotal())
                            .borrowed(i.getBorrowed())
                            .safety(i.getSafety())
                            .userId(i.getUserId())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    public UserRiskStatisticsDTO sumUserRisk(Long orgId) {
        StatisticsUserRiskRequest request = StatisticsUserRiskRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        StatisticsUserRiskResponse response = marginClient.statisticsUserRisk(request);

        if (Objects.isNull(response) || Objects.isNull(response.getUserRiskSum())) {
            return new UserRiskStatisticsDTO();
        }

        return UserRiskStatisticsDTO.builder()
                .warnNum(response.getUserRiskSum().getWarmNum())
                .appendNum(response.getUserRiskSum().getAppendNum())
                .closeNum(response.getUserRiskSum().getCloseNum())
                .build();
    }

    @Override
    public ForceCloseResponse forceClose(Long orgId, Long accountId, Long adminUserId, String desc) {
        ForceCloseRequest request = ForceCloseRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setAccountId(accountId)
                .setAdminUserId(adminUserId)
                .setDesc(desc == null ? "" : desc)
                .build();
        return marginClient.forceClose(request);
    }

    @Override
    public RiskStatisticsDTO sumAllRisk(Long orgId) {
        StatisticsRiskRequest request = StatisticsRiskRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        StatisticsRiskResponse response = marginClient.statisticsRisk(request);

        if (Objects.isNull(response) || Objects.isNull(response.getRiskSum())) {
            return new RiskStatisticsDTO();
        }

        return RiskStatisticsDTO.builder()
                .userNum(response.getRiskSum().getUserNum())
                .loanValue(response.getRiskSum().getLoanValue())
                .allValue(response.getRiskSum().getAllValue())
                .averageSafety(response.getRiskSum().getAverageSafety())
                .loanOrderNum(response.getRiskSum().getLoanOrderNum())
                .todayLoanOrderNum(response.getRiskSum().getTodayLoanOrderNum())
                .loanUserNum(response.getRiskSum().getLoanUserNum())
                .build();
    }

    @Override
    public List<DailyRiskStatisticsDTO> queryRptDailyStatisticsRisk(Long orgId, Long toTime, Integer limit) {

        QueryRptDailyStatisticsRiskRequest request = QueryRptDailyStatisticsRiskRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setToTime(toTime)
                .setLimit(limit)
                .build();

        QueryRptDailyStatisticsRiskResponse response = marginClient.queryRptDailyStatisticsRisk(request);

        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getDailyListList())) {
            return new ArrayList<>();
        }

        return response.getDailyListList().stream().map(risk -> DailyRiskStatisticsDTO.builder()
                .date(risk.getDate())
                .averageSafety(risk.getAverageSafety())
                .loanValue(risk.getLoanValue())
                .usdtLoanValue(risk.getUsdtLoanValue())
                .allValue(risk.getAllValue())
                .usdtAllValue(risk.getUsdtAllValue())
                .userNum(risk.getUserNum())
                .loanUserNum(risk.getLoanUserNum())
                .loanOrderNum(risk.getLoanOrderNum())
                .todayLoanOrderNum(risk.getTodayLoanOrderNum())
                .todayPayNum(risk.getTodayPayNum())
                .todayLoanUserNum(risk.getTodayLoanUserNum())
                .build())
                .collect(Collectors.toList());

    }

    @Override
    public List<UserPositionDTO> queryUserPosition(Long orgId, Long accountId, Long userId) {
        if (accountId == 0 && userId == 0) {
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER, "accountId and userId is 0");
        }
        QueryUserPositionRequest request = QueryUserPositionRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setAccountId(accountId)
                .build();
        QueryUserPositionResponse response = marginClient.queryPositionDetail(request);

        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getPositionDetailList())) {
            return Lists.newArrayList();
        }

        return response.getPositionDetailList().stream().map(
                i -> {
                    return UserPositionDTO.builder()
                            .locked(i.getLocked())
                            .available(i.getAvailable())
                            .total(i.getTotal())
                            .tokenId(i.getTokenId())
                            .borrowed(i.getBorrow())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> queryForceClose(Long orgId, QueryForceClosePO po) {
        if (po.getAccountId() == 0 && po.getUserId() == 0) {
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER, "accountId and userId is 0");
        }
        QueryForceCloseRequest request = QueryForceCloseRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(po.getUserId()).build())
                .setAccountId(po.getAccountId())
                .setFromOrderId(po.getFromOrderId())
                .setEndOrderId(po.getEndOrderId())
                .setLimit(po.getLimit())
                .build();
        QueryForceCloseResponse response = marginClient.queryForceClose(request);

        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getForceCloseList())) {
            return Lists.newArrayList();
        }

        return response.getForceCloseList().stream().map(
                i -> {
                    return OrderDTO.builder()
                            .time(Long.valueOf(i.getTime()))
                            .orderId(Long.valueOf(i.getOrderId()))
                            .accountId(Long.valueOf(i.getAccountId()))
                            .symbolId(i.getSymbolId())
                            .symbolName(i.getSymbolName())
                            .baseTokenId(i.getBaseTokenId())
                            .baseTokenName(i.getBaseTokenName())
                            .quoteTokenId(i.getQuoteTokenId())
                            .quoteTokenName(i.getQuoteTokenName())
                            .price(i.getPrice())
                            .origQty(i.getOrigQty())
                            .executedQty(i.getExecutedQty())
                            .executedAmount(i.getExecutedAmount())
                            .avgPrice(i.getAvgPrice())
                            .type(i.getOrderType().name())
                            .side(i.getOrderSide().name())
                            .status(i.getStatus().name())
                            .statusDesc(i.getStatusDesc())
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<InterestConfigDTO> queryMarginInterestByLevel(Long orgId, Long levelConfigId) {
        if (levelConfigId < 0L) {
            levelConfigId = 0L;
        }
        QueryInterestByLevelRequest request = QueryInterestByLevelRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId))
                .setLevelConfigId(levelConfigId)
                .build();
        QueryInterestByLevelResponse response = marginClient.queryInteresyBtLevel(request);
        return response.getInterestConfigList().stream().map(this::getInterestConfigDto).collect(Collectors.toList());

    }

    @Override
    public List<ForceRecordDTO> queryForceRecord(Long orgId, Long accoutId, Long userId, Long fromId, Long toId, Long startTime, Long endTime, Integer limit) {
        AdminQueryForceRecordRequest request = AdminQueryForceRecordRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setAccountId(accoutId)
                .setFromId(fromId)
                .setToId(toId)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setLimit(limit)
                .build();
        AdminQueryForceRecordResponse recordResponse = marginClient.adminQueryForceRecord(request);
        return recordResponse.getForceRecordsList().stream()
                .map(record -> {
                    ForceRecordDTO dto = new ForceRecordDTO();
                    dto.setId(record.getId());
                    dto.setForceId(record.getForceId());
                    dto.setOrgId(record.getOrgId());
                    dto.setAdminUserId(record.getAdminUserId());
                    dto.setAccountId(record.getAccountId());
                    dto.setSafety(record.getSafety());
                    dto.setAllPosition(record.getAllPosition());
                    dto.setAllLoan(record.getAllLoan());
                    dto.setForceType(record.getForceType());
                    dto.setDealStatus(record.getDealStatus());
                    dto.setCreated(record.getCreated());
                    dto.setUpdated(record.getUpdated());
                    dto.setUserId(record.getUserId());
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public MarginPositionStatusDTO queryMarginPositionStatus(Long orgId, Long userId) {
        AdminQueryAccountStatusRequest request = AdminQueryAccountStatusRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId)).build();
        AdminQueryAccountStatusResponse response = marginClient.adminQueryAccountStatus(request);
        return MarginPositionStatusDTO.builder()
                .userId(response.getUserId())
                .accountId(response.getAccountId())
                .curStatus(response.getCurStatus())
                .build();
    }

    @Override
    public AdminChangeMarginPositionStatusResponse changeMarginPositionStatus(Long orgId, Long userId, Integer changeToStatus, Integer curStatus) {
        AdminChangeMarginPositionStatusRequest request = AdminChangeMarginPositionStatusRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId))
                .setChangeToStatus(changeToStatus)
                .setCurStatus(curStatus)
                .build();
        return marginClient.adminChangeMarginPosition(request);
    }

    @Override
    public List<AccountLoanLimitDTO> queryAccountLoanLimt(Long orgId, Long userId, Integer vipLevel) {
        QueryAccountLoanLimitVIPRequest request = QueryAccountLoanLimitVIPRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setVipLevel(vipLevel)
                .build();
        QueryAccountLoanLimitVIPResponse response = marginClient.queryAccountLoanLimitVIP(request);
        return response.getDatasList().stream().map(data -> AccountLoanLimitDTO.builder()
                .id(data.getId())
                .orgId(data.getOrgId())
                .userId(data.getUserId())
                .accountId(data.getAccountId())
                .vipLevel(data.getVipLevel())
                .created(data.getCreated())
                .updated(data.getUpdated())
                .build()).collect(Collectors.toList());
    }

    @Override
    public SetAccountLoanLimitVIPResponse setAccountLoanLimit(Long orgId, Integer vipLevel, String userIds) {
        SetAccountLoanLimitVIPRequest request = SetAccountLoanLimitVIPRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setVipLevel(vipLevel)
                .setUserIds(userIds)
                .build();
        return marginClient.setAccountLoanLimitVIP(request);
    }

    @Override
    public DeleteAccountLoanLimitVIPResponse deleteAccountLoanLimit(Long orgId, Long userId) {
        DeleteAccountLoanLimitVIPRequest request = DeleteAccountLoanLimitVIPRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId)
                        .setUserId(userId)
                        .build())
                .build();
        return marginClient.deleteAccountLoanLimitVIP(request);
    }

    @Override
    public List<MarginPoolRptDTO> queryMarginPoolRpt(Long orgId, String tokenId) {
        QueryRptMarginPoolRequest request = QueryRptMarginPoolRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTokenId(tokenId)
                .build();
        QueryRptMarginPoolResponse response = marginClient.queryRptMarginPool(request);
        if (response.getRet() != 0) {
            log.error("queryMarginPoolRpt error :{}", response.getRet());
            return new ArrayList<>();
        }
        return response.getDataList().stream()
                .map(pool -> MarginPoolRptDTO.builder()
                        .id(pool.getId())
                        .orgId(pool.getOrgId())
                        .tokenId(pool.getTokenId())
                        .beginAmount(new BigDecimal(pool.getAvailable()).add(new BigDecimal(pool.getUnpaidAmount())).subtract(new BigDecimal(pool.getInterestPaid())).stripTrailingZeros().toPlainString())
                        .available(pool.getAvailable())
                        .unpaidAmount(pool.getUnpaidAmount())
                        .interestUnpaid(pool.getInterestUnpaid())
                        .interestPaid(pool.getInterestPaid())
                        .totalInterest(new BigDecimal(pool.getInterestPaid()).add(new BigDecimal(pool.getInterestUnpaid())).stripTrailingZeros().toPlainString())
                        .created(pool.getCreated())
                        .updated(pool.getUpdated())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<MarginRiskBlackListDTO> queryMarginRiskBlackList(Long orgId, Long userId, String confGroup) {
        QueryMarginRiskBlackListRequest request = QueryMarginRiskBlackListRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setConfGroup(confGroup)
                .build();
        QueryMarginRiskBlackListResponse response = marginClient.queryMarginRiskBlackList(request);
        if (response.getRet() != 0) {
            return new ArrayList<>();
        }
        return response.getDataList().stream().map(data -> MarginRiskBlackListDTO.builder()
                .id(data.getId())
                .orgId(data.getOrgId())
                .confGroup(data.getConfGroup())
                .userId(data.getUserId())
                .accountId(data.getAccountId())
                .adminUserName(data.getAdminUserName())
                .reason(data.getReason())
                .created(data.getCreated())
                .updated(data.getUpdated())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public AddMarginRiskBlackListResponse addMarginRiskBlackList(Long orgId, Long userId, String confGroup, String adminUserName, String reason) {
        AddMarginRiskBlackListRequest request = AddMarginRiskBlackListRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setConfGroup(confGroup)
                .setAdminUserName(adminUserName)
                .setReason(reason)
                .build();
        return marginClient.addMarginRiskBlackList(request);
    }

    @Override
    public DelMarginRiskBlackListResponse delMarginRiskBlackList(Long orgId, Long userId, String confGroup) {
        DelMarginRiskBlackListRequest request = DelMarginRiskBlackListRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setConfGroup(confGroup)
                .build();
        return marginClient.delMarginRiskBlackList(request);
    }

    @Override
    public List<RptMarginTradeDTO> queryRptMarginTrade(Long orgId, Long toTime, Integer limit) {
        QueryRptMarginTradeRequest request = QueryRptMarginTradeRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setToTime(toTime)
                .setLimit(limit)
                .build();
        QueryRptMarginTradeResponse response = marginClient.queryRptMarginTrade(request);
        if (response.getRet() != 0) {
            return new ArrayList<>();
        }
        return response.getDataList().stream()
                .map(trade -> RptMarginTradeDTO.builder()
                        .id(trade.getId())
                        .orgId(trade.getOrgId())
                        .createTime(trade.getCreateTime())
                        .tradePeopleNum(trade.getTradePeopleNum())
                        .buyPeopleNum(trade.getBuyPeopleNum())
                        .sellPeopleNum(trade.getSellPeopleNum())
                        .tradeNum(trade.getTradeNum())
                        .buyTradeNum(trade.getBuyTradeNum())
                        .sellTradeNum(trade.getSellTradeNum())
                        .fee(trade.getFee())
                        .amount(trade.getAmount())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<RptMarginTradeDetailDTO> queryRptMarginTradeDetail(Long orgId, Long relationId) {
        QueryRptMarginTradeDetailRequest request = QueryRptMarginTradeDetailRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setRelationId(relationId)
                .build();
        QueryRptMarginTradeDetailResponse response = marginClient.queryRptMarginTradeDetail(request);
        if (response.getRet() != 0) {
            return new ArrayList<>();
        }
        return response.getDataList().stream()
                .map(trade -> RptMarginTradeDetailDTO.builder()
                        .relationId(trade.getRelationId())
                        .orgId(trade.getOrgId())
                        .symbolId(trade.getSymbolId())
                        .tradePeopleNum(trade.getTradePeopleNum())
                        .buyPeopleNum(trade.getBuyPeopleNum())
                        .sellPeopleNum(trade.getSellPeopleNum())
                        .tradeNum(trade.getTradeNum())
                        .buyTradeNum(trade.getBuyTradeNum())
                        .sellTradeNum(trade.getSellTradeNum())
                        .baseFee(trade.getBaseFee())
                        .quoteFee(trade.getQuoteFee())
                        .fee(trade.getFee())
                        .amount(trade.getAmount())
                        .created(trade.getCreated())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public SetSpecialInterestResponse setSpecialInterestConfig(Long orgId, String tokenId, String interest, Long userId, Integer effectiveFlag) {
        SetSpecialInterestRequest request = SetSpecialInterestRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setTokenId(tokenId)
                .setShowInterest(interest)
                .setEffectiveFlag(effectiveFlag)
                .build();
        return marginClient.setSpecialInterest(request);
    }

    @Override
    public List<SpecialInterestDTO> querySpecialInterestConfig(Long orgId, String tokenId, Long userId, Long accountId) {
        QuerySpecialInterestRequest request = QuerySpecialInterestRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setUserId(userId)
                .setAccountId(accountId)
                .setTokenId(tokenId)
                .build();
        QuerySpecialInterestResponse response = marginClient.querySpecialInterest(request);
        if (response.getRet() != 0) {
            return new ArrayList();
        }
        BigDecimal hundred = new BigDecimal("100");
        return response.getDataList().stream()
                .map(interest -> SpecialInterestDTO.builder()
                        .id(interest.getId())
                        .tokenId(interest.getTokenId())
                        .orgId(interest.getOrgId())
                        .userId(interest.getUserId())
                        .accountId(interest.getAccountId())
                        .interest(new BigDecimal(interest.getInterest()).multiply(hundred).stripTrailingZeros().toPlainString())
                        .showInterest(new BigDecimal(interest.getShowInterest()).multiply(hundred).stripTrailingZeros().toPlainString())
                        .effectiveFlag(interest.getEffectiveFlag())
                        .created(interest.getCreated())
                        .updated(interest.getUpdated())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public DeleteSpecialInterestResponse delSpecialInterestConfig(Long orgId, String tokenId, Long userId) {
        DeleteSpecialInterestRequest request = DeleteSpecialInterestRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setTokenId(tokenId)
                .build();
        return marginClient.deleteSpecialInterest(request);
    }

    @Override
    public List<OpenMarginActivityDTO> queryOpenMarginActivity(Long orgId, Long userId, Long accountId, Long startTime, Long endTime, Long fromId, Integer joinStatus, Integer limit) {
        AdminQueryOpenMarginActivityRequest request = AdminQueryOpenMarginActivityRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setAccountId(accountId)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setFromId(fromId)
                .setJoinStatus(joinStatus)
                .setLimit(limit)
                .build();
        AdminQueryOpenMarginActivityResponse response = marginClient.queryOpenMarginActivity(request);
        if (response.getRet() > 0) {
            return new ArrayList<>();
        }
        return response.getDataList().stream()
                .map(data -> OpenMarginActivityDTO.builder()
                        .id(data.getId())
                        .orgId(data.getOrgId())
                        .userId(data.getUserId())
                        .accountId(data.getAccountId())
                        .submitTime(data.getSubmitTime())
                        .kycLevel(data.getKycLevel())
                        .allPositionUsdt(data.getAllPositionUsdt())
                        .lotteryNo(data.getLotteryNo())
                        .created(data.getCreated())
                        .updated(data.getUpdated())
                        .build())
                .collect(Collectors.toList());

    }

    @Override
    public AdminCheckDayOpenMarginActivityResponse adminCheckDayOpenMarginActivity(Long orgId) {
        AdminCheckDayOpenMarginActivityRequest request = AdminCheckDayOpenMarginActivityRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        return marginClient.adminCheckDayOpenMarginActivity(request);
    }

    @Override
    public AdminCheckMonthOpenMarginActivityResponse adminCheckMonthOpenMarginActivity(Long orgId) {
        AdminCheckMonthOpenMarginActivityRequest request = AdminCheckMonthOpenMarginActivityRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        return marginClient.adminCheckMonthOpenMarginActivity(request);
    }

    @Override
    public List<MarginProfitDTO> queryMarginProfitActivity(Long orgId, Long userId, Long beginDate, Long endDate, Integer joinStatus, Long fromId, Long accountId, Integer limit) {
        AdminQueryProfitActivityRequest request = AdminQueryProfitActivityRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setBeginDate(beginDate)
                .setEndDate(endDate)
                .setJoinStatus(joinStatus)
                .setFromId(fromId)
                .setAccountId(accountId)
                .setLimit(limit)
                .build();
        AdminQueryProfitActivityResponse response = marginClient.adminQueryProfitActivity(request);
        return response.getDataList().stream().map(this::getMarginProfitResult).collect(Collectors.toList());

    }

    private MarginProfitDTO getMarginProfitResult(ProfitActivity activity) {
        return MarginProfitDTO.builder()
                .id(activity.getId())
                .orgId(activity.getOrgId())
                .joinDate(activity.getJoinDate())
                .userId(activity.getUserId())
                .accountId(activity.getAccountId())
                .submitTime(activity.getSubmitTime())
                .kycLevel(activity.getKycLevel())
                .profitRate(activity.getProfitRate())
                .allPositionUsdt(activity.getAllPositionUsdt())
                .todayPositionUsdt(activity.getTodayPositionUsdt())
                .joinStatus(activity.getJoinStatus())
                .dayRanking(activity.getDayRanking())
                .updates(activity.getUpdates())
                .updated(activity.getUpdated())
                .created(activity.getCreated())
                .build();
    }

    @Override
    public AdminSortProfitRankingResponse adminSortProfitRanking(Long orgId) {
        AdminSortProfitRankingRequest request = AdminSortProfitRankingRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId))
                .build();
        return marginClient.adminSortProfitRanking(request);
    }

    @Override
    public AdminSetProfitRankingResponse adminSetProfitRanking(Long orgId, Long joinDate, Long userId, Integer ranking) {
        AdminSetProfitRankingRequest request = AdminSetProfitRankingRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setJoinDate(joinDate)
                .setRanking(ranking)
                .build();
        return marginClient.adminSetProfitRanking(request);
    }

    @Override
    public AdminRecalTopProfitRateResponse adminRecalTopProfitRate(Long orgId, Long joinDate, Integer top) {
        AdminRecalTopProfitRateRequst requst = AdminRecalTopProfitRateRequst.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId))
                .setJoinDate(joinDate)
                .setTop(top)
                .build();
        return marginClient.adminRecalTopProfitRate(requst);
    }

    /**
     * 设置特殊借币限额 -- 新系统可暂不支持
     * @param orgId
     * @param userId
     * @param loanLimit
     * @param isOpen
     * @return
     */
    @Override
    public AdminSetSpecialLoanLimitResponse adminSetSpecialLoanLimit(Long orgId, Long userId, String loanLimit, Integer isOpen) {
        AdminSetSpecialLoanLimitRequest request = AdminSetSpecialLoanLimitRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setLoanLimit(loanLimit)
                .setIsOpen(isOpen)
                .build();

        return marginClient.adminSetSpecialLoanLimit(request);
    }
    /**
     * 查询特殊借币限额 -- 新系统可暂不支持
     * @param userId
     * @return
     */
    @Override
    public List<SpecialLoanLimitDTO> querySpecialLoanLimit(Long orgId, Long userId) {
        AdminQuerySpecialLoanLimitRequest request = AdminQuerySpecialLoanLimitRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .build();
        AdminQuerySpecialLoanLimitResponse response = marginClient.adminQuerySpecialLoanLimit(request);
        if (response.getRet() != 0) {
            return new ArrayList<>();
        }
        return response.getDataList().stream()
                .map(limit -> SpecialLoanLimitDTO.builder()
                        .id(limit.getId())
                        .orgId(limit.getOrgId())
                        .userId(limit.getUserId())
                        .accountId(limit.getAccountId())
                        .loanLimit(limit.getLoanLimit())
                        .isOpen(limit.getIsOpen())
                        .created(limit.getCreated())
                        .updated(limit.getUpdated())
                        .build())
                .collect(Collectors.toList());
    }
    /**
     * 删除特殊借币限额 -- 新系统可暂不支持
     * @param userId
     * @return
     */
    @Override
    public AdminDelSpecialLoanLimitResponse adminDelSpecialLoanLimit(Long orgId, Long userId) {
        AdminDelSpecialLoanLimitRequest request = AdminDelSpecialLoanLimitRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId))
                .build();

        return marginClient.adminDelSpecialLoanLimit(request);
    }

    @Override
    public AdminSetMarginLoanLimitResponse adminSetMarginLoanLimit(Long orgId, String tokenId, String limitAmount, Integer status) {
        AdminSetMarginLoanLimitRequest requst = AdminSetMarginLoanLimitRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTokenId(tokenId)
                .setLimitAmount(limitAmount)
                .setStatus(status)
                .build();

        return marginClient.adminSetMarginLoanLimit(requst);
    }

    @Override
    public List<MarginLoanLimitDTO> adminQueryMarginLoanLimit(Long orgId, String tokenId) {
        AdminQueryMarginLoanLimitRequest requst = AdminQueryMarginLoanLimitRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTokenId(tokenId)
                .build();

        AdminQueryMarginLoanLimitResponse response = marginClient.adminQueryMarginLoanLimit(requst);

        return response.getLimitsList().stream().map(limit -> {
            MarginLoanLimitDTO dto = new MarginLoanLimitDTO();
            dto.setId(limit.getId());
            dto.setOrgId(limit.getOrgId());
            dto.setTokenId(limit.getTokenId());
            dto.setLimitAmount(limit.getLimitAmount());
            dto.setStatus(limit.getStatus());
            dto.setCreated(limit.getCreated());
            dto.setUpdated(limit.getUpdated());
            return dto;
        }).collect(Collectors.toList());

    }

    @Override
    public AdminSetMarginUserLoanLimitResponse adminSetMarginUserLoanLimit(Long orgId, Long userId, String tokenId, String limitAmount, Integer status) {

        AdminSetMarginUserLoanLimitRequest requst = AdminSetMarginUserLoanLimitRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setUserId(userId)
                .setTokenId(tokenId)
                .setLimitAmount(limitAmount)
                .setStatus(status)
                .build();

        return marginClient.adminSetMarginUserLoanLimit(requst);

    }

    @Override
    public List<MarginUserLoanLimitDTO> adminQueryMarginUserLoanLimit(Long orgId, String tokenId, Long userId, Long accountId) {
        AdminQueryMarginUserLoanLimitRequest requst = AdminQueryMarginUserLoanLimitRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setAccountId(accountId)
                .setUserId(userId)
                .setTokenId(tokenId)
                .build();

        AdminQueryMarginUserLoanLimitResponse response = marginClient.adminQueryMarginUserLoanLimit(requst);

        return response.getLimitsList().stream().map(limit -> {
            MarginUserLoanLimitDTO dto = new MarginUserLoanLimitDTO();
            dto.setId(limit.getId());
            dto.setOrgId(limit.getOrgId());
            dto.setUserId(limit.getUserId());
            dto.setAccountId(limit.getAccountId());
            dto.setTokenId(limit.getTokenId());
            dto.setLimitAmount(limit.getLimitAmount());
            dto.setStatus(limit.getStatus());
            dto.setCreated(limit.getCreated());
            dto.setUpdated(limit.getUpdated());
            return dto;
        }).collect(Collectors.toList());
    }
}
