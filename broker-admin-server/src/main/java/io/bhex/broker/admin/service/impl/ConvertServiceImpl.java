package io.bhex.broker.admin.service.impl;

import com.google.common.collect.Lists;
import io.bhex.broker.admin.controller.dto.ConvertOrderDTO;
import io.bhex.broker.admin.controller.dto.ConvertSymbolDTO;
import io.bhex.broker.admin.controller.dto.FundAccountDTO;
import io.bhex.broker.admin.controller.param.ConvertOrderQueryPO;
import io.bhex.broker.admin.controller.param.ConvertSymbolModifyPO;
import io.bhex.broker.admin.controller.param.ConvertSymbolCreatePO;
import io.bhex.broker.admin.controller.param.ConvertSymbolStatusUpdatePO;
import io.bhex.broker.admin.grpc.client.ConvertClient;
import io.bhex.broker.admin.service.ConvertService;
import io.bhex.broker.grpc.admin.QueryFundAccountShowRequest;
import io.bhex.broker.grpc.admin.QueryFundAccountShowResponse;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.convert.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: cookie.yuan
 * @CreateDate: 16/08/2018
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class ConvertServiceImpl implements ConvertService {

    @Autowired
    private ConvertClient convertClient;

    @Override
    public AddConvertSymbolResponse addConvertSymbol(ConvertSymbolCreatePO po, Long orgId) {
        AddConvertSymbolRequest request = AddConvertSymbolRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setBrokerAccountId(po.getBrokerAccountId())
                .setPurchaseTokenId(po.getPurchaseTokenId())
                .setOfferingsTokenId(po.getOfferingsTokenId())
                .setPurchasePrecision(po.getPurchasePrecision())
                .setOfferingsPrecision(po.getOfferingsPrecision())
                .setPriceType(po.getPriceType())
                .setPriceValue(po.getPriceValue())
                .setSymbolId(po.getSymbolId())
                .setMinQuantity(po.getMinQuantity())
                .setMaxQuantity(po.getMaxQuantity())
                .setAccountDailyLimit(po.getAccountDailyLimit())
                .setAccountTotalLimit(po.getAccountTotalLimit())
                .setSymbolDailyLimit(po.getSymbolDailyLimit())
                .setVerifyKyc(po.getVerifyKyc())
                .setVerifyMobile(po.getVerifyMobile())
                .setVerifyVipLevel(po.getVerifyVipLevel())
                .setStatus(po.getStatus())
                .build();
        return convertClient.addConvertSymbol(request);
    }

    @Override
    public List<ConvertSymbolDTO> queryConvertSymbol(Long orgId) {
        GetConvertSymbolsRequest request = GetConvertSymbolsRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        GetConvertSymbolsResponse response = convertClient.getConvertSymbols(request);
        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getConvertSymbolList())) {
            return Lists.newArrayList();
        }

        return response.getConvertSymbolList().stream().map(
                symbol -> ConvertSymbolDTO.builder()
                        .convertSymbolId(symbol.getConvertSymbolId())
                        .brokerId(symbol.getBrokerId())
                        .brokerAccountId(symbol.getBrokerAccountId())
                        .symbolId(symbol.getSymbolId())
                        .purchaseTokenId(symbol.getPurchaseTokenId())
                        .purchaseTokenName(symbol.getPurchaseTokenName())
                        .offeringsTokenId(symbol.getOfferingsTokenId())
                        .offeringsTokenName(symbol.getOfferingsTokenName())
                        .purchasePrecision(symbol.getPurchasePrecision())
                        .offeringsPrecision(symbol.getOfferingsPrecision())
                        .priceType(symbol.getPriceType())
                        .priceValue(symbol.getPriceValue())
                        .minQuantity(symbol.getMinQuantity())
                        .maxQuantity(symbol.getMaxQuantity())
                        .accountDailyLimit(symbol.getAccountDailyLimit())
                        .accountTotalLimit(symbol.getAccountTotalLimit())
                        .symbolDailyLimit(symbol.getSymbolDailyLimit())
                        .verifyKyc(symbol.getVerifyKyc())
                        .verifyMobile(symbol.getVerifyMobile())
                        .verifyVipLevel(symbol.getVerifyVipLevel())
                        .status(symbol.getStatus())
                        .created(symbol.getCreated())
                        .updated(symbol.getUpdated())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public ModifyConvertSymbolResponse modifyConvertSymbol(ConvertSymbolModifyPO po, Long orgId) {
        ModifyConvertSymbolRequest request = ModifyConvertSymbolRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setConvertSymbolId(po.getConvertSymbolId())
                .setBrokerAccountId(po.getBrokerAccountId())
                .setPurchaseTokenId(po.getPurchaseTokenId())
                .setOfferingsTokenId(po.getOfferingsTokenId())
                .setPurchasePrecision(po.getPurchasePrecision())
                .setOfferingsPrecision(po.getOfferingsPrecision())
                .setPriceType(po.getPriceType())
                .setPriceValue(po.getPriceValue())
                .setSymbolId(po.getSymbolId())
                .setMinQuantity(po.getMinQuantity())
                .setMaxQuantity(po.getMaxQuantity())
                .setAccountDailyLimit(po.getAccountDailyLimit())
                .setAccountTotalLimit(po.getAccountTotalLimit())
                .setSymbolDailyLimit(po.getSymbolDailyLimit())
                .setVerifyKyc(po.getVerifyKyc())
                .setVerifyMobile(po.getVerifyMobile())
                .setVerifyVipLevel(po.getVerifyVipLevel())
                .build();
        return convertClient.modifyConvertSymbol(request);
    }

    @Override
    public UpdateConvertSymbolStatusResponse updateConvertSymbolStatus(ConvertSymbolStatusUpdatePO po, Long orgId) {
        UpdateConvertSymbolStatusRequest request = UpdateConvertSymbolStatusRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setConvertSymbolId(po.getConvertSymbolId())
                .setStatus(po.getStatus())
                .build();
        return convertClient.updateConvertSymbolStatus(request);
    }

    @Override
    public List<ConvertOrderDTO> queryConvertOrders(ConvertOrderQueryPO po, Long orgId) {
        AdminQueryConvertOrdersRequest request = AdminQueryConvertOrdersRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setUserId(po.getUserId())
                .setConvertSymbolId(po.getConvertSymbolId())
                .setStatus(po.getStatus())
                .setBeginTime(po.getBeginTime())
                .setEndTime(po.getEndTime())
                .setStartId(po.getLastId())
                .setCount(po.getPageSize())
                .build();
        AdminQueryConvertOrdersResponse response = convertClient.queryConvertOrders(request);
        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getConvertOrderList())) {
            return Lists.newArrayList();
        }

        return response.getConvertOrderList().stream().map(
                order -> ConvertOrderDTO.builder()
                        .id(order.getId())
                        .orderId(order.getOrderId())
                        .userId(order.getUserId())
                        .accountId(order.getAccountId())
                        .brokerAccountId(order.getBrokerAccountId())
                        .convertSymbolId(order.getConvertSymbolId())
                        .purchaseTokenId(order.getPurchaseTokenId())
                        .purchaseTokenName(order.getPurchaseTokenName())
                        .offeringsTokenId(order.getOfferingsTokenId())
                        .offeringsTokenName(order.getOfferingsTokenName())
                        .purchaseQuantity(order.getPurchaseQuantity())
                        .offeringsQuantity(order.getOfferingsQuantity())
                        .price(order.getPrice())
                        .status(order.getStatus())
                        .errorMessage(order.getErrorMessage())
                        .created(order.getCreated())
                        .updated(order.getUpdated())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<FundAccountDTO> queryFundAccountShow(Long orgId) {
        QueryFundAccountShowResponse response = convertClient.queryFundAccountShow(
                QueryFundAccountShowRequest.newBuilder().setOrgId(orgId).build());
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
}
