package io.bhex.broker.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.CalcProductRebatePO;
import io.bhex.broker.admin.controller.param.StakingProductListPO;
import io.bhex.broker.admin.controller.param.StakingProductPO;
import io.bhex.broker.admin.grpc.client.StakingClient;
import io.bhex.broker.admin.grpc.client.impl.StakingClientImpl;
import io.bhex.broker.admin.service.StakingService;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.common.exception.BrokerException;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.common.util.StringUtil;
import io.bhex.broker.grpc.admin.QueryFundAccountShowRequest;
import io.bhex.broker.grpc.admin.QueryFundAccountShowResponse;
import io.bhex.broker.grpc.proto.AdminCommonResponse;
import io.bhex.broker.grpc.staking.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StakingServiceImpl implements StakingService {

    @Autowired
    StakingClient stakingClient;

    @Autowired
    StakingClientImpl stakingClientImpl;

    @Override
    public ResultModel saveProduct(StakingProductPO stakingProductPO) {

        StakingProductInfo productInfo = null;
        if (stakingProductPO.getId() > 0) {
            //产品信息修改
            AdminGetProductDetailRequest request = AdminGetProductDetailRequest.newBuilder()
                    .setOrgId(stakingProductPO.getOrgId())
                    .setProductId(stakingProductPO.getId())
                    .build();

            AdminGetProductDetailReply getProductDetailReply = stakingClient.getProductDetail(request);
            if (!getProductDetailReply.hasProductInfo()) {
                return ResultModel.error("product.is.not.exist");
            }

            productInfo = getProductDetailReply.getProductInfo();
        }

        long currentTimeMillis = System.currentTimeMillis();

        try {
            validProduct(stakingProductPO, productInfo, currentTimeMillis);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
            return ResultModel.error(e.getMessage());
        }

        List<AdminSaveProductRequest.ProductLocalInfo> localInfos = stakingProductPO.getLocalInfos().stream().map(this::convert2ProductLocalInfo).collect(Collectors.toList());

        List<AdminSaveProductRequest.ProductRebateInfo> rebateInfos = new ArrayList<>();
        if (stakingProductPO.getRebates() != null) {
            rebateInfos = stakingProductPO.getRebates().stream().map(rebate -> convert2ProductRebateInfo(rebate, stakingProductPO.getRebateCalcWay())).collect(Collectors.toList());
        }

        AdminSaveProductRequest request = AdminSaveProductRequest.newBuilder()
                .setOrgId(stakingProductPO.getOrgId())
                .setId(stakingProductPO.getId())
                .setTokenId(stakingProductPO.getTokenId())
                .setDividendType(stakingProductPO.getDividendType())
                .setDividendTimes(stakingProductPO.getDividendTimes())
                .setTimeLimit(stakingProductPO.getTimeLimit())
                .setReferenceApr(stakingProductPO.getReferenceApr())
                .setActualApr(stakingProductPO.getType() == StakingProductType.FI_CURRENT_VALUE ? stakingProductPO.getActualApr().stripTrailingZeros().toPlainString() : "0")
                .setWeeklyApr("0")
                .setPerUsrLowLots(stakingProductPO.getPerUsrLowLots())
                .setPerUsrUpLots(stakingProductPO.getPerUsrUpLots())
                .setUpLimitLots(stakingProductPO.getUpLimitLots())
                .setShowUpLimitLots(stakingProductPO.getShowUpLimitLots())
                .setPerLotAmount(stakingProductPO.getPerLotAmount())
                .setSubscribeStartDate(stakingProductPO.getSubscribeStartDate())
                .setSubscribeEndDate(stakingProductPO.getSubscribeEndDate())
                .setInterestStartDate(stakingProductPO.getInterestStartDate())
                .setSort(stakingProductPO.getSort())
                .setType(stakingProductPO.getType())
                .setPrincipalAccountId(stakingProductPO.getPrincipalAccountId())
                .setDividendAccountId(stakingProductPO.getDividendAccountId())
                .setArrposid(stakingProductPO.getArrposid())
                .addAllLocalInfos(localInfos)
                .addAllRebates(rebateInfos)
                .setLimitinfo(convert2ProductSubscribeLimitInfo(stakingProductPO.getLimit()))
                .setTimestamp(currentTimeMillis)
                .build();

        AdminSaveProductReply saveProductReply = stakingClient.saveProduct(request);

        // 0=成功 1=本金账户不是有效资金账户 2=派息账户不是有效资金账户 3=产品信息不存在 4=不创建产品权限
        if (saveProductReply.getCode() == 1) {
            return ResultModel.error("principalAccountId.is.invalid");
        } else if (saveProductReply.getCode() == 2) {
            return ResultModel.error("dividendAccountId.is.invalid");
        } else if (saveProductReply.getCode() == 3) {
            return ResultModel.error("product.is.not.exist");
        } else if (saveProductReply.getCode() == 4) {
            return ResultModel.error("no.permission.to.create.product");
        } else {
            return ResultModel.ok(saveProductReply.getProductId());
        }
    }

    @Override
    public StakingProductDTO getProductDetail(Long orgId, Long productId) {
        AdminGetProductDetailRequest request = AdminGetProductDetailRequest.newBuilder()
                .setOrgId(orgId)
                .setProductId(productId)
                .build();

        AdminGetProductDetailReply getProductDetailReply = stakingClient.getProductDetail(request);
        StakingProductInfo productInfo = getProductDetailReply.getProductInfo();

        return convert2ProductDTO(productInfo);
    }

    @Override
    public List<StakingProductProfileDTO> getProductList(Long orgId, String language, StakingProductListPO po) {

        List<Integer> types = po.getProductType() == null ? new ArrayList<>() : Collections.singletonList(po.getProductType());

        AdminGetProductListRequest request = AdminGetProductListRequest.newBuilder()
                .setOrgId(orgId)
                .setLanguage(language)
                .addAllProductType(types)
                .setStartProductId(po.getLastId())
                .setLimit(po.getLimit())
                .build();

        AdminGetProductListReply getProductListReply = stakingClient.getProductList(request);

        return getProductListReply.getProductsList().stream().map(this::convert2ProductProfileDTO).collect(Collectors.toList());
    }

    @Override
    public boolean onlineProduct(Long orgId, Long productId, Integer status) {
        AdminOnlineProductRequest request = AdminOnlineProductRequest.newBuilder()
                .setOrgId(orgId)
                .setProductId(productId)
                .setOnlineStatus(status == 1)
                .build();

        AdminCommonResponse adminCommonResponse = stakingClient.onlineProduct(request);
        if (!adminCommonResponse.getSuccess()) {
            log.warn("onlineStatus fail,productId={},brokerId={},message={}", productId, orgId, adminCommonResponse.getMsg());
        }
        return adminCommonResponse.getSuccess();
    }

    @Override
    public StakingProductPermissionDTO getBrokerProductPermission(Long orgId) {
        AdminGetBrokerProductPermissionRequest request = AdminGetBrokerProductPermissionRequest.newBuilder()
                .setBrokerId(orgId)
                .build();
        AdminGetBrokerProductPermissionReply reply = stakingClient.getBrokerProductPermission(request);
        StakingProductPermissionDTO dto = new StakingProductPermissionDTO();
        dto.setAllowFixed(reply.getAllowFixed());
        dto.setAllowFixedLock(reply.getAllowFixedLock());
        dto.setAllowCurrent(reply.getAllowCurrent());
        return dto;
    }

    @Override
    public List<StakingProductRebateDTO> queryBrokerProductUndoRebate(Long orgId, String language, Integer productType) {
        AdminQueryBrokerProductUndoRebateRequest rebateRequest = AdminQueryBrokerProductUndoRebateRequest.newBuilder()
                .setOrgId(orgId)
                .setLanguage(language)
                .setProductType(productType)
                .build();

        AdminQueryBrokerProductUndoRebateReply reply = stakingClient.queryBrokerProductUndoRebate(rebateRequest);

        List<StakingProductRebateDTO> dtoList = new ArrayList<>();
        reply.getRebatesList().forEach(rebate -> {
            StakingProductRebateDTO dto = new StakingProductRebateDTO();
            dto.setId(rebate.getId());
            dto.setProductId(rebate.getProductId());
            dto.setProductName(rebate.getProductName());
            dto.setProductType(rebate.getProductType());
            dto.setDividendType(rebate.getDividendType());
            dto.setTokenId(rebate.getTokenId());
            dto.setTokenName(rebate.getTokenName());
            dto.setRebateDate(rebate.getRebateDate());
            dto.setPrincipalAmount(rebate.getPrincipalAmount());
            dto.setInterestAmount(rebate.getInterestAmount());
            dto.setInterestTokenId(rebate.getInterestTokenId());
            dto.setInterestTokenName(rebate.getInterestTokenName());
            dto.setRebateRate(rebate.getRebateRate());
            dto.setStatus(rebate.getStatus());
            dto.setNumberOfPeriods(rebate.getNumberOfPeriods());
            dto.setType(rebate.getType());
            dto.setRebateCalcWay(rebate.getRebateCalcWay());
            dto.setRebateAmount(rebate.getRebateAmount());
            dtoList.add(dto);
        });

        return dtoList;
    }

    @Override
    public List<StakingProductRebateDTO> queryBrokerProductHistoryRebate(Long orgId, String language, Integer productType, Integer pageNo, Integer size) {
        AdminQueryBrokerProductHistoryRebateRequest rebateRequest = AdminQueryBrokerProductHistoryRebateRequest.newBuilder()
                .setOrgId(orgId)
                .setLanguage(language)
                .setProductType(productType)
                .setPageNo(pageNo)
                .setSize(size)
                .build();

        AdminQueryBrokerProductHistoryRebateReply reply = stakingClient.queryBrokerProductHistoryRebate(rebateRequest);

        List<StakingProductRebateDTO> dtoList = new ArrayList<>();
        reply.getRebatesList().forEach(rebate -> {
            StakingProductRebateDTO dto = new StakingProductRebateDTO();
            dto.setId(rebate.getId());
            dto.setProductId(rebate.getProductId());
            dto.setProductName(rebate.getProductName());
            dto.setProductType(rebate.getProductType());
            dto.setDividendType(rebate.getDividendType());
            dto.setTokenId(rebate.getTokenId());
            dto.setTokenName(rebate.getTokenName());
            dto.setRebateDate(rebate.getRebateDate());
            dto.setPrincipalAmount(rebate.getPrincipalAmount());
            dto.setInterestAmount(rebate.getInterestAmount());
            dto.setInterestTokenId(rebate.getInterestTokenId());
            dto.setInterestTokenName(rebate.getInterestTokenName());
            dto.setRebateRate(rebate.getRebateRate());
            dto.setStatus(rebate.getStatus());
            dto.setNumberOfPeriods(rebate.getNumberOfPeriods());
            dto.setUpdateAt(rebate.getUpdatedAt());
            dto.setType(rebate.getType());
            dto.setRebateCalcWay(rebate.getRebateCalcWay());
            dto.setRebateAmount(rebate.getRebateAmount());
            dtoList.add(dto);
        });

        return dtoList;
    }


    @Override
    public Boolean cancelDividend(Long orgId, Long productId, Long productRebateId) {

        StakingProductCancelDividendRequest request = StakingProductCancelDividendRequest.newBuilder()
                .setOrgId(orgId)
                .setProductId(productId)
                .setProductRebateId(productRebateId)
                .build();

        StakingProductCancelDividendResponse response = stakingClient.cancelDividend(request);
        if (response.getRet() != BrokerErrorCode.SUCCESS.code()) {
            log.warn("cancelDividend fail,orgId={},productId={},productRebateId={}", orgId, productId, productRebateId);
        }

        return response.getRet() == BrokerErrorCode.SUCCESS.code();
    }

    @Override
    public Integer dividendTransfer(Long orgId, Long productId, Long productRebateId) {

        StakingProductDividendTransferRequest request = StakingProductDividendTransferRequest.newBuilder()
                .setOrgId(orgId)
                .setProductId(productId)
                .setProductRebateId(productRebateId)
                .build();

        StakingProductDividendTransferResponse response = stakingClient.dividendTransfer(request);
        if (response.getRet() != BrokerErrorCode.SUCCESS.code()) {
            log.warn("dividendTransfer fail,orgId={},productId={},productRebateId={}", orgId, productId, productRebateId);
        }

        return response.getRet();
    }

    public List<FundAccountDTO> queryFundAccountShow(Long orgId) {
        QueryFundAccountShowResponse response = stakingClientImpl.queryFundAccountShow(QueryFundAccountShowRequest.newBuilder().setOrgId(orgId).build());

        List<FundAccountDTO> dtoList = new ArrayList<>();
        for (QueryFundAccountShowResponse.FundAccountShow fundAccountShow : response.getFundAccountsList()) {
            FundAccountDTO dto = new FundAccountDTO();
            dto.setId(fundAccountShow.getId());
            dto.setOrgId(fundAccountShow.getOrgId());
            dto.setUserId(fundAccountShow.getUserId());
            dto.setAccountId(fundAccountShow.getAccountId());
            dto.setTag(fundAccountShow.getTag());
            dto.setRemark(fundAccountShow.getRemark());
            dto.setIsShow(fundAccountShow.getIsShow());

            dtoList.add(dto);
        }

        return dtoList;
    }

    public List<StakingProductOrderDTO> queryBrokerProductOrder(Long orgId, Long productId, Long userId, String phone, String email, Long orderId, Long startId, Integer limit) {

        limit = limit > 100 ? 100 : limit;

        QueryBrokerProductOrderRequest request = QueryBrokerProductOrderRequest.newBuilder()
                .setLimit(limit != null ? limit : 30)
                .setOrgId(orgId)
                .setProductId(productId)
                .setUserId(userId != null ? userId : 0L)
                .setPhone(phone != null ? phone : "")
                .setEmail(email != null ? email : "")
                .setStartId(startId != null ? startId : 0L)
                .setOrderId(orderId != null ? orderId : 0L)
                .build();

        QueryBrokerProductOrderReply reply = stakingClientImpl.queryBrokerProductOrder(request);

        return reply.getOrdersList().stream().map(order -> {
            StakingProductOrderDTO dto = new StakingProductOrderDTO();
            dto.setOrderId(order.getOrderId());
            dto.setUserId(order.getUserId());
            dto.setProductId(order.getProductId());
            dto.setProductName(order.getProductName());
            dto.setDividendType(order.getDividendType());
            dto.setTokenId(order.getTokenId());
            dto.setTokenName(order.getTokenName());
            dto.setOrderType(order.getOrderType());
            dto.setTimeLimit(order.getTimeLimit());
            dto.setPayLots(order.getPayLots());
            dto.setPayAmount(order.getPayAmount());
            dto.setTakeEffectDate(order.getTakeEffectDate());
            dto.setProductEndDate(order.getProductEndDate());
            dto.setRedemptionDate(order.getRedemptionDate());
            dto.setStatus(order.getStatus());
            dto.setReferenceApr(order.getReferenceApr());
            dto.setCreatedAt(order.getCreatedAt());
            return dto;

        }).collect(Collectors.toList());

    }

    public List<CurrentProductAssetDTO> queryCurrentProductAsset(Long orgId, Long productId, Long userId, String phone, String email, Long startId, Integer limit, String language) {

        limit = limit > 100 ? 100 : limit;

        AdminQueryCurrentProductAssetRequest request = AdminQueryCurrentProductAssetRequest.newBuilder()
                .setLimit(limit != null ? limit : 30)
                .setOrgId(orgId)
                .setProductId(productId)
                .setUserId(userId != null ? userId : 0L)
                .setPhone(phone != null ? phone : "")
                .setEmail(email != null ? email : "")
                .setStartId(startId != null ? startId : 0L)
                .setLanguage(language)
                .build();

        AdminQueryCurrentProductAssetReply reply = stakingClientImpl.queryCurrentProductAsset(request);

        return reply.getAssetsList().stream().map(asset -> {
            CurrentProductAssetDTO dto = new CurrentProductAssetDTO();
            dto.setId(asset.getId());
            dto.setUserId(asset.getUserId());
            dto.setProductId(asset.getProductId());
            dto.setProductName(asset.getProductName());
            dto.setProductEndDate(asset.getProductEndDate());
            dto.setTokenId(asset.getTokenId());
            dto.setTokenName(asset.getTokenName());
            dto.setAmount(asset.getAmount());
            dto.setProductStatus(asset.getProductStatus());
            return dto;

        }).collect(Collectors.toList());

    }

    public List<CurrentProductRebateDTO> getCurrentProductRebateList(Long orgId, Long productId, Integer status, Long startRebateDate, Integer size) {

        GetCurrentProductRebateListRequest request = GetCurrentProductRebateListRequest.newBuilder().setOrgId(orgId)
                .setProductId(productId)
                .setStatus(status)
                .setStartRebateDate(startRebateDate)
                .setSize(size)
                .build();

        GetCurrentProductRebateListReply reply = stakingClientImpl.getCurrentProductRebateList(request);

        return reply.getRebatesList().stream().map(rebate ->
                CurrentProductRebateDTO.builder()
                        .rebateDate(rebate.getRebateDate())
                        .rebateRate(new BigDecimal(rebate.getRebateRate()))
                        .status(rebate.getStatus())
                        .build()).collect(Collectors.toList());

    }

    public List<StakingProductRepaymentScheduleDTO> getProductRepaymentSchedule(Long orgId, Long orderId) {

        GetProductRepaymentScheduleRequest request = GetProductRepaymentScheduleRequest.newBuilder().setOrgId(orgId).setOrderId(orderId).build();

        GetProductRepaymentScheduleReply reply = stakingClientImpl.getProductRepaymentSchedule(request);

        return reply.getSchedulesList().stream().map(schedule -> {
            StakingProductRepaymentScheduleDTO dto = new StakingProductRepaymentScheduleDTO();
            dto.setUserId(schedule.getUserId());
            dto.setProductId(schedule.getProductId());
            dto.setOrderId(schedule.getOrderId());
            dto.setSort(schedule.getSort());
            dto.setRebateDate(schedule.getRebateDate());
            dto.setRebateRate(schedule.getRebateRate());
            dto.setRebateAmount(schedule.getRebateAmount());
            dto.setTokenId(schedule.getTokenId());
            dto.setTokenName(schedule.getTokenName());
            dto.setStatus(schedule.getStatus());
            return dto;

        }).collect(Collectors.toList());

    }

    public List<CurrentProductRepaymentScheduleDTO> getCurrentProductRepaymentSchedule(Long orgId, Long userId, Long productId, Long startId, Integer size) {

        GetCurrentProductRepaymentScheduleRequest request = GetCurrentProductRepaymentScheduleRequest.newBuilder()
                .setOrgId(orgId)
                .setUserId(userId)
                .setProductId(productId)
                .setStartId(startId)
                .setSize(size)
                .build();

        GetCurrentProductRepaymentScheduleReply reply = stakingClientImpl.getCurrentProductRepaymentSchedule(request);

        return reply.getSchedulesList().stream().map(schedule -> {
            CurrentProductRepaymentScheduleDTO dto = new CurrentProductRepaymentScheduleDTO();
            dto.setUserId(schedule.getUserId());
            dto.setProductId(schedule.getProductId());
            dto.setRebateDate(schedule.getRebateDate());
            dto.setRebateRate(schedule.getRebateRate());
            dto.setRebateAmount(schedule.getRebateAmount());
            dto.setTokenId(schedule.getTokenId());
            dto.setTokenName(schedule.getTokenName());
            return dto;

        }).collect(Collectors.toList());

    }

    public void calcProductRebate(Long orgId, CalcProductRebatePO po) {

        if (po.getRebateRate() != null && po.getRebateRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("staking.product.rebateRate.must.not.less.than.0");
        }

        if (StringUtils.isNotBlank(po.getRebateAmount()) && new BigDecimal(po.getRebateAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("staking.product.rebateAmount.must.not.less.than.0");
        }

        StakingCalcInterestRequest request = StakingCalcInterestRequest.newBuilder()
                .setOrgId(orgId)
                .setProductId(po.getProductId())
                .setProductRebateId(po.getProductRebateId())
                .setRebateRate(po.getRebateRate().stripTrailingZeros().toPlainString())
                .setRebateAmount(po.getRebateAmount())
                .setTokenId(po.getTokenId())
                .build();

        StakingCalcInterestReply reply = stakingClientImpl.calcInterest(request);
        if (reply.getCode() != 0) {
            BrokerErrorCode code = BrokerErrorCode.fromCode(reply.getCode());
            if (code == null) {
                code = BrokerErrorCode.UNCAUGHT_EXCEPTION;
            }
            throw new BrokerException(code);
        }
    }


    /**
     * proto 的StakingProductProfile 转为 StakingProductProfileDTO
     *
     * @param productProfile
     * @return
     */
    private StakingProductProfileDTO convert2ProductProfileDTO(StakingProductProfile productProfile) {
        return StakingProductProfileDTO.builder()
                .id(productProfile.getProductId())
                .productName(productProfile.getProductName())
                .tokenId(productProfile.getTokenId())
                .tokenName(productProfile.getTokenName())
                .subscribeStartDate(productProfile.getSubscribeStartDate())
                .subscribeEndDate(productProfile.getSubscribeEndDate())
                .interestStartDate(productProfile.getInterestStartDate())
                .type(productProfile.getType())
                .isShow(productProfile.getIsShow())
                .dividendType(productProfile.getDividendType())
                .status(productProfile.getStatus())
                .totalAmount(new BigDecimal(productProfile.getUpLimitLots()).multiply(new BigDecimal(productProfile.getPerLotAmount())).stripTrailingZeros().toPlainString())
                .soldAmount(new BigDecimal(productProfile.getSoldLots()).multiply(new BigDecimal(productProfile.getPerLotAmount())).stripTrailingZeros().toPlainString())
                .build();
    }

    /**
     * proto 的StakingProductInfo 转为StakingProductDTO
     *
     * @param productInfo
     * @return
     */
    private StakingProductDTO convert2ProductDTO(StakingProductInfo productInfo) {
        StakingProductDTO dto = new StakingProductDTO();
        dto.setId(productInfo.getId());
        dto.setOrgId(productInfo.getOrgId());
        dto.setTokenId(productInfo.getTokenId());
        dto.setTokenName(productInfo.getTokenName());
        dto.setDividendType(productInfo.getDividendType());
        dto.setDividendTimes(productInfo.getDividendTimes());
        dto.setTimeLimit(productInfo.getTimeLimit());
        dto.setReferenceApr(productInfo.getReferenceApr());
        dto.setActualApr(new BigDecimal(productInfo.getActualApr()));
        dto.setPerUsrLowLots(productInfo.getPerUsrLowLots());
        dto.setPerUsrUpLots(productInfo.getPerUsrUpLots());
        dto.setUpLimitLots(productInfo.getUpLimitLots());
        dto.setShowUpLimitLots(productInfo.getShowUpLimitLots());
        dto.setSoldLots(productInfo.getSoldLots());
        dto.setPerLotAmount(productInfo.getPerLotAmount());
        dto.setSubscribeStartDate(productInfo.getSubscribeStartDate());
        dto.setSubscribeEndDate(productInfo.getSubscribeEndDate());
        dto.setInterestStartDate(productInfo.getInterestStartDate());
        dto.setSort(productInfo.getSort());
        dto.setType(productInfo.getType());
        dto.setIsShow(productInfo.getIsShow());
        dto.setPrincipalAccountId(productInfo.getPrincipalAccountId());
        dto.setDividendAccountId(productInfo.getDividendAccountId());
        dto.setFundFlow(productInfo.getFundFlow());
        dto.setArrposid(productInfo.getArrposid());
        dto.setCreatedAt(productInfo.getCreatedAt());
        dto.setUpdatedAt(productInfo.getUpdatedAt());
        dto.setRebateCalcWay(productInfo.getRebateCalcWay());

        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis < dto.getSubscribeStartDate()) {
            //募集前
            dto.setStatus(0);
        } else if (currentTimeMillis >= dto.getSubscribeStartDate() && currentTimeMillis < dto.getSubscribeEndDate()) {
            //募集中
            dto.setStatus(1);
        } else if (currentTimeMillis >= dto.getSubscribeEndDate() && currentTimeMillis < dto.getInterestStartDate()) {
            //募集结束且计息前
            dto.setStatus(2);
        } else if (currentTimeMillis >= dto.getInterestStartDate() && currentTimeMillis < dto.getInterestStartDate() + dto.getTimeLimit() * 86400_000L) {
            //计息中
            dto.setStatus(4);
        } else {
            //已结束
            dto.setStatus(5);
        }

        StakingProductDTO.SubscribeLimit subscribeLimit = new StakingProductDTO.SubscribeLimit();
        subscribeLimit.setLevelLimit(productInfo.getLimitinfo().getLevelLimit());
        subscribeLimit.setVerifyAvgBalance(productInfo.getLimitinfo().getVerifyAvgBalance() == 1);
        subscribeLimit.setVerifyBalance(productInfo.getLimitinfo().getVerifyBalance() == 1);
        subscribeLimit.setVerifyBindPhone(productInfo.getLimitinfo().getVerifyBindPhone() == 1);
        subscribeLimit.setVerifyKyc(productInfo.getLimitinfo().getVerifyKyc() == 1);

        Map<String, String> map = new HashMap<>();
        if (StringUtils.isNotEmpty(productInfo.getLimitinfo().getBalanceRuleJson())) {
            try {
                map = JsonUtil.defaultGson().fromJson(productInfo.getLimitinfo().getBalanceRuleJson(), Map.class);
            } catch (JsonSyntaxException e) {
                log.error("verifyPurchaseLimit error: Balance Rule Json=> {}, projectId=> {}", productInfo.getLimitinfo().getBalanceRuleJson(), productInfo.getId());
            }
        }

        String quantitySnapshot = map.get("positionVolume") != null ? map.get("positionVolume") : "";
        String tokenSnapshot = map.get("positionToken") != null ? map.get("positionToken") : "";
        String verifyAvgBalanceToken = map.get("verifyAvgBalanceToken") != null ? map.get("verifyAvgBalanceToken") : "";
        String verifyAvgBalanceVolume = map.get("verifyAvgBalanceVolume") != null ? map.get("verifyAvgBalanceVolume") : "";
        String verifyAvgBalanceStartTime = map.get("verifyAvgBalanceStartTime") != null ? map.get("verifyAvgBalanceStartTime") : "0";
        String verifyAvgBalanceEndTime = map.get("verifyAvgBalanceEndTime") != null ? map.get("verifyAvgBalanceEndTime") : "0";

        subscribeLimit.setQuantity(quantitySnapshot);
        subscribeLimit.setPositionToken(tokenSnapshot);
        subscribeLimit.setVerifyAvgBalanceToken(verifyAvgBalanceToken);
        subscribeLimit.setVerifyAvgBalanceVolume(verifyAvgBalanceVolume);
        subscribeLimit.setVerifyAvgBalanceStartTime(Long.parseLong(verifyAvgBalanceStartTime));
        subscribeLimit.setVerifyAvgBalanceEndTime(Long.parseLong(verifyAvgBalanceEndTime));
        dto.setLimit(subscribeLimit);

        List<StakingProductDTO.LocalInfo> localInfoList = productInfo.getLocalInfosList().stream().map(local -> {
            StakingProductDTO.LocalInfo localInfo = new StakingProductDTO.LocalInfo();
            localInfo.setLanguage(local.getLang());
            localInfo.setProductName(local.getProductName());
            localInfo.setProductDetails(local.getProductDetails());
            localInfo.setProtocolUrl(local.getProtocolUrl());
            localInfo.setEnabled(local.getStatus() == 1);
            return localInfo;
        }).collect(Collectors.toList());

        List<StakingProductDTO.Rebate> rebateList = productInfo.getRebatesList().stream().map(rebate -> {
            StakingProductDTO.Rebate rebateInfo = new StakingProductDTO.Rebate();
            rebateInfo.setId(rebate.getId());
            rebateInfo.setTokenId(rebate.getTokenId());
            rebateInfo.setTokenName(rebate.getTokenName());
            rebateInfo.setRebateDate(rebate.getRebateDate());
            rebateInfo.setRebateRate(new BigDecimal(rebate.getRebateRate()));
            rebateInfo.setRebateAmount(rebate.getRebateAmount());
            if (currentTimeMillis + 1800_000 > rebate.getRebateDate()) {
                rebateInfo.setCanModify(0);
            } else {
                rebateInfo.setCanModify(1);
            }
            return rebateInfo;
        }).collect(Collectors.toList());

        dto.setLocalInfos(localInfoList);
        dto.setRebates(rebateList);
        return dto;
    }

    /**
     * @param localInfo
     * @return
     */
    private AdminSaveProductRequest.ProductLocalInfo convert2ProductLocalInfo(StakingProductPO.LocalInfo localInfo) {
        return AdminSaveProductRequest.ProductLocalInfo.newBuilder()
                .setProductName(localInfo.getProductName())
                .setLanguage(localInfo.getLanguage())
                .setProductDetails(localInfo.getProductDetails())
                .setProtocolUrl(localInfo.getProtocolUrl())
                .setBackgroundUrl(localInfo.getBackgroundUrl())
                .build();
    }

    /**
     * @param rebate
     * @return
     */
    private AdminSaveProductRequest.ProductRebateInfo convert2ProductRebateInfo(StakingProductPO.Rebate rebate, Integer rebateCalcWay) {
        return AdminSaveProductRequest.ProductRebateInfo.newBuilder()
                .setId(rebate.getId() == null ? 0 : rebate.getId())
                .setRebateDate(rebate.getRebateDate())
                .setRebateRate(rebateCalcWay == StakingProductRebateCalcWay.STPD_REBATE_CALCWAY_RATE_VALUE ? rebate.getRebateRate().stripTrailingZeros().toPlainString() : "0")
                .setTokenId(rebate.getTokenId())
                .setRebateAmount(rebateCalcWay == StakingProductRebateCalcWay.STPD_REBATE_CALCWAY_AMOUNT_VALUE ? rebate.getRebateAmount() : "0")
                .setRebateCalcWay(rebateCalcWay)
                .build();
    }

    /**
     * @param limit
     * @return
     */
    private AdminSaveProductRequest.ProductSubscribeLimitInfo convert2ProductSubscribeLimitInfo(StakingProductPO.SubscribeLimit limit) {

        Map<String, String> map = Maps.newHashMap();
        if (limit.getVerifyBalance()) {
            map.put("positionToken", limit.getPositionToken());
            map.put("positionVolume", limit.getQuantity());
        }

        if (limit.getVerifyAvgBalance()) {
            map.put("verifyAvgBalanceToken", limit.getVerifyAvgBalanceToken());
            map.put("verifyAvgBalanceVolume", limit.getVerifyAvgBalanceVolume());
            map.put("verifyAvgBalanceStartTime", limit.getVerifyAvgBalanceStartTime() > 0 ? String.valueOf(limit.getVerifyAvgBalanceStartTime()) : "");
            map.put("verifyAvgBalanceEndTime", limit.getVerifyAvgBalanceStartTime() > 0 ? String.valueOf(limit.getVerifyAvgBalanceEndTime()) : "");
        }

        return AdminSaveProductRequest.ProductSubscribeLimitInfo.newBuilder()
                .setVerifyKyc(limit.getVerifyKyc() ? 1 : 0)
                .setVerifyBindPhone(limit.getVerifyBindPhone() ? 1 : 0)
                .setVerifyBalance(limit.getVerifyBalance() ? 1 : 0)
                .setVerifyAvgBalance(limit.getVerifyAvgBalance() ? 1 : 0)
                .setBalanceRuleJson(map.size() > 0 ? JSON.toJSONString(map) : "")
                .setLevelLimit(limit.getLevelLimit())
                .build();
    }


    private void validProduct(StakingProductPO po, StakingProductInfo oldProductInfo, long currentTimeMillis) {

        //活期暂不开放
//        if (StakingProductType.forNumber(po.getType()) == null || po.getType() == StakingProductType.FI_CURRENT_VALUE) {
//            throw new IllegalArgumentException("staking.product.type.error");
//        }

        if (StakingProductRebateCalcWay.forNumber(po.getRebateCalcWay()) == null
                || (oldProductInfo != null && po.getRebateCalcWay() != oldProductInfo.getRebateCalcWay())
                || (po.getType() == StakingProductType.FI_CURRENT_VALUE && po.getRebateCalcWay() != StakingProductRebateCalcWay.STPD_REBATE_CALCWAY_RATE_VALUE)) {
            throw new IllegalArgumentException("staking.product.rebate.calc.way.error");
        }

        //修改产品且当前时间大于认购开始时间
        if (oldProductInfo != null && oldProductInfo.getSubscribeStartDate() < currentTimeMillis && oldProductInfo.getType() != po.getType()) {
            throw new IllegalArgumentException("staking.product.type.error");
        }

        if (StakingProductDividendType.forNumber(po.getDividendType()) == null
                || (po.getType() == StakingProductType.FI_CURRENT_VALUE && po.getDividendType() != StakingProductDividendType.STPD_DIVIDENT_INSTALLMENT_VALUE)) {
            throw new IllegalArgumentException("staking.product.dividend.type.error");
        }

        //修改产品且当前时间大于认购开始时间
        if (oldProductInfo != null && oldProductInfo.getSubscribeStartDate() < currentTimeMillis && oldProductInfo.getDividendType() != po.getDividendType()) {
            throw new IllegalArgumentException("staking.product.dividend.type.error");
        }

        if (po.getDividendTimes() <= 0 || (po.getDividendType() == StakingProductDividendType.STPD_DIVIDENT_ONE_OFF_VALUE && po.getDividendTimes() != 1)) {
            throw new IllegalArgumentException("staking.product.dividend.times.error");
        }

        if (po.getTimeLimit() <= 0 && po.getType() != StakingProductType.FI_CURRENT_VALUE) {
            throw new IllegalArgumentException("staking.product.time.limit.error");
        }

        if (po.getPerUsrLowLots() < 0) {
            throw new IllegalArgumentException("staking.product.perUsrLowLots.error");
        }

        if (po.getPerUsrUpLots().compareTo(po.getPerUsrLowLots()) < 0) {
            throw new IllegalArgumentException("staking.product.perUsrUpLots.must.not.less.than.perUsrLowLots");
        }

        //去掉对于 实际发售份额不能小于最大购买份额的限制 2021年01月19日
//        if (po.getUpLimitLots().compareTo(po.getPerUsrUpLots()) < 0) {
//            throw new IllegalArgumentException("staking.product.upLimitLots.must.not.less.than.perUsrUpLots");
//        }

        if (po.getUpLimitLots() < (oldProductInfo == null ? 0 : oldProductInfo.getSoldLots())) {
            throw new IllegalArgumentException("staking.product.upLimitLots.must.not.less.than.soldLots");
        }

        if (po.getShowUpLimitLots().compareTo(po.getUpLimitLots()) < 0 && po.getType() != StakingProductType.FI_CURRENT_VALUE) {
            throw new IllegalArgumentException("staking.product.showUpLimitLots.must.not.less.than.upLimitLots");
        }

        if (new BigDecimal(po.getPerLotAmount()).compareTo(BigDecimal.ZERO) != 1) {
            throw new IllegalArgumentException("staking.product.perLotAmount.must.greater.than.zero");
        }

        if ((oldProductInfo == null || oldProductInfo.getSubscribeStartDate() > currentTimeMillis) && po.getSubscribeStartDate().compareTo(currentTimeMillis) <= 0) {
            throw new IllegalArgumentException("staking.product.subscribeStartDate.must.later.than.now");
        }

        if (po.getSubscribeEndDate().compareTo(po.getSubscribeStartDate()) < 0) {
            throw new IllegalArgumentException("staking.product.subscribeEndDate.must.no.earlier.than.subscribeStartDate");
        }

        if (po.getType() == StakingProductType.FI_CURRENT_VALUE) {

            if (po.getActualApr().compareTo(BigDecimal.ZERO) == -1) {
                throw new IllegalArgumentException("staking.product.rebateRate.must.not.less.than.0");
            }

            if ((po.getSubscribeEndDate() - po.getSubscribeStartDate()) % 86400_000L > 0) {
                throw new IllegalArgumentException("staking.product.the.duration.of.the.project.should.be.integer");
            }

            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(po.getSubscribeStartDate());
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(po.getSubscribeEndDate());

            int startYear = start.get(Calendar.YEAR);
            int startMonth = start.get(Calendar.MONTH);
            int startDay = start.get(Calendar.DAY_OF_MONTH);

            int endYear = end.get(Calendar.YEAR);
            int endMonth = end.get(Calendar.MONTH);
            int endDay = end.get(Calendar.DAY_OF_MONTH);

            int year = endYear - startYear;
            int month = endMonth - startMonth;
            int day = endDay - startDay;
            if (year > 5 || (year == 5 && (month > 1 || day > 1))) {
                throw new IllegalArgumentException("staking.product.the.duration.of.the.project.should.be.less.than.5.years");
            }
        }

        if (po.getInterestStartDate().compareTo(po.getSubscribeEndDate()) <= 0 && po.getType() != StakingProductType.FI_CURRENT_VALUE) {
            throw new IllegalArgumentException("staking.product.interestStartDate.must.later.than.subscribeEndDate");
        }

        //计息开始时间需为整点
        if (po.getInterestStartDate() % 3600_000 > 0 && po.getType() != StakingProductType.FI_CURRENT_VALUE) {
            throw new IllegalArgumentException("staking.product.interestStartDate.must.be.on.the.hour");
        }

        if (CollectionUtils.isEmpty(po.getLocalInfos())) {
            throw new IllegalArgumentException("staking.product.localInfos.must.can.not.be.empty");
        }

        for (StakingProductPO.LocalInfo localInfo : po.getLocalInfos()) {
            if (StringUtils.isBlank(localInfo.getLanguage())) {
                throw new IllegalArgumentException("staking.product.language.is.necessary");
            }

            if (StringUtils.isBlank(localInfo.getProductName())) {
                throw new IllegalArgumentException("staking.product.productName.is.necessary");
            }

            if (StringUtil.getLength(localInfo.getProductName(), "utf-8") > 64) {
                throw new IllegalArgumentException("staking.product.productName.is.exceed.64.characters");
            }
        }

        long productEndDate = po.getInterestStartDate() + po.getTimeLimit() * 86400_000L;

        //定期或者锁仓检查派息列表
        if (po.getType() == StakingProductType.FI_TIME_VALUE || po.getType() == StakingProductType.LOCK_POSITION_VALUE) {

            if (CollectionUtils.isEmpty(po.getRebates())) {
                throw new IllegalArgumentException("staking.product.rebates.must.can.not.be.empty");
            }

            if (po.getRebates().size() != po.getDividendTimes()) {
                throw new IllegalArgumentException("staking.product.rebates.count.error");
            }

            Set<Long> rebateDatesSet = new HashSet<>();
            Set<Long> rebateIdSet = new HashSet<>();

            for (StakingProductPO.Rebate rebate : po.getRebates()) {
                if (rebateDatesSet.contains(rebate.getRebateDate())) {
                    throw new IllegalArgumentException("staking.product.rebateDate.can.not.repeat");
                }

                if (rebate.getRebateDate() == null || rebate.getRebateDate().compareTo(po.getInterestStartDate()) <= 0 || rebate.getRebateDate().compareTo(productEndDate) > 0) {
                    throw new IllegalArgumentException("staking.product.rebateDate.error");
                }

                if (po.getRebateCalcWay() == StakingProductRebateCalcWay.STPD_REBATE_CALCWAY_RATE_VALUE) {
                    //按利率计算利息

                    //派息币种为产品发行币种
                    rebate.setTokenId(po.getTokenId());

                    //派息时间间隔需整数天数
                    if ((rebate.getRebateDate() - po.getInterestStartDate()) % 86400_000L > 0) {
                        throw new IllegalArgumentException("staking.product.rebateDate.interval.must.be.day");
                    }

                    //利率不能小于0
                    if (rebate.getRebateRate().compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("staking.product.rebateRate.must.not.less.than.0");
                    }
                } else {
                    //按金额分配利息
                    if (StringUtils.isBlank(rebate.getTokenId())) {
                        throw new IllegalArgumentException("staking.product.rebate.tokenId.error");
                    }

                    if (new BigDecimal(rebate.getRebateAmount()).compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("staking.product.rebateAmount.must.not.less.than.0");
                    }
                }


                rebateDatesSet.add(rebate.getRebateDate());
                rebateIdSet.add(rebate.getId());
            }

            //修改产品且当前时间大于认购开始时间
            if (oldProductInfo != null && oldProductInfo.getSubscribeStartDate() < currentTimeMillis) {
                Set<Long> oldRebateIdSet = oldProductInfo.getRebatesList().stream().map(StakingProductRebateInfo::getId).collect(Collectors.toSet());

                oldRebateIdSet.removeAll(rebateIdSet);
                if (oldRebateIdSet.size() > 0) {
                    throw new IllegalArgumentException("staking.product.rebates.error");
                }
            }

            if (!rebateDatesSet.contains(productEndDate)) {
                throw new IllegalArgumentException("staking.product.productEndDate.must.be.rebateDate");
            }
        }

        if (po.getLimit().getVerifyBalance()) {

            if (StringUtils.isBlank(po.getLimit().getPositionToken())) {
                throw new IllegalArgumentException("staking.product.verify_balance.token.empty");
            }

            if (new BigDecimal(po.getLimit().getQuantity()).compareTo(BigDecimal.ZERO) < 1) {
                throw new IllegalArgumentException("staking.product.verify_balance.volume.empty");
            }
        }

        if (po.getLimit().getVerifyAvgBalance()) {
            if (po.getLimit().getVerifyAvgBalanceEndTime().equals(0)) {
                throw new IllegalArgumentException("staking.product.verify_avg_balance.end.time.empty");
            }

            if (po.getLimit().getVerifyAvgBalanceStartTime().equals(0)) {
                throw new IllegalArgumentException("staking.product.verify_avg_balance.start.time.empty");
            }

            if (StringUtils.isEmpty(po.getLimit().getVerifyAvgBalanceToken())) {
                throw new IllegalArgumentException("staking.product.verify_avg_balance.token.empty");
            }

            if (StringUtils.isEmpty(po.getLimit().getVerifyAvgBalanceVolume())) {
                throw new IllegalArgumentException("staking.product.verify_avg_balance.volume.empty");
            }

            if (new DateTime(po.getLimit().getVerifyAvgBalanceStartTime()).toString("yyyy-MM-dd")
                    .equals(new DateTime(po.getLimit().getVerifyAvgBalanceEndTime()).toString("yyyy-MM-dd"))) {
                throw new IllegalArgumentException("staking.product.verify_avg_balance.time.limit");
            }
        }


    }

}
