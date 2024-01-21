package io.bhex.broker.admin.service.impl;

import com.google.common.collect.Lists;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.OtcThirdPartyClient;
import io.bhex.broker.admin.service.OtcThirdPartyService;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.otc.third.party.*;
import io.bhex.broker.grpc.otc.third.party.OtcThirdPartyDisclaimer;
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
public class OtcThirdPartyServiceImpl implements OtcThirdPartyService {

    @Autowired
    private OtcThirdPartyClient otcThirdPartyClient;

    @Override
    public List<OtcThirdPartyDTO> getOtcThirdParty(Long orgId) {
        GetOtcThirdPartyRequest request = GetOtcThirdPartyRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        GetOtcThirdPartyResponse response = otcThirdPartyClient.getOtcThirdParty(request);
        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getThirdPartyList())) {
            return Lists.newArrayList();
        }
        return response.getThirdPartyList().stream().map(
                thirdParty -> OtcThirdPartyDTO.builder()
                        .thirdPartyId(thirdParty.getThirdPartyId())
                        .thirdPartyName(thirdParty.getThirdPartyName())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<OtcThirdPartyDisclaimerDTO> queryOtcThirdPartyDisclaimer(Long orgId) {
        QueryOtcThirdPartyDisclaimerRequest request = QueryOtcThirdPartyDisclaimerRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        QueryOtcThirdPartyDisclaimerResponse response = otcThirdPartyClient.queryOtcThirdPartyDisclaimer(request);
        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getThirdPartyDisclaimerList())) {
            return Lists.newArrayList();
        }

        return response.getThirdPartyDisclaimerList().stream().map(
                disclaimer -> OtcThirdPartyDisclaimerDTO.builder()
                        .orgId(disclaimer.getOrgId())
                        .thirdPartyId(disclaimer.getThirdPartyId())
                        .language(disclaimer.getLanguage())
                        .disclaimer(disclaimer.getDisclaimer())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public UpdateOtcThirdPartyDisclaimerResponse updateOtcThirdPartyDisclaimer(OtcThirdPartyDisclaimerUpdatePO po, Long orgId) {
        List<OtcThirdPartyDisclaimer> disclaimerList = new ArrayList<>();
        for (io.bhex.broker.admin.controller.param.OtcThirdPartyDisclaimer disclaimer : po.getDisclaimerList()) {
            disclaimerList.add(OtcThirdPartyDisclaimer.newBuilder()
                    .setOrgId(orgId)
                    .setThirdPartyId(disclaimer.getThirdPartyId())
                    .setLanguage(disclaimer.getLanguage())
                    .setDisclaimer(disclaimer.getDisclaimer())
                    .build());
        }
        UpdateOtcThirdPartyDisclaimerRequest request = UpdateOtcThirdPartyDisclaimerRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .addAllThirdPartyDisclaimer(disclaimerList)
                .build();
        return otcThirdPartyClient.updateOtcThirdPartyDisclaimer(request);
    }

    @Override
    public List<OtcThirdPartyOrderDTO> queryOtcThirdPartyOrders(OtcThirdPartyOrderQueryPO po, Long orgId) {
        QueryOtcThirdPartyOrdersByAdminRequest request = QueryOtcThirdPartyOrdersByAdminRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setOrderId(po.getOrderId())
                .setUserId(po.getUserId())
                .setStatus(po.getStatus())
                .setBeginTime(po.getStartTime())
                .setEndTime(po.getEndTime())
                .setFromOrderId(po.getLastId())
                .setCount(po.getLimit())
                .build();
        QueryOtcThirdPartyOrdersResponse response = otcThirdPartyClient.queryOtcThirdPartyOrdersByAdmin(request);
        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getOtcThirdPartyOrderList())) {
            return Lists.newArrayList();
        }

        return response.getOtcThirdPartyOrderList().stream().map(
                order -> OtcThirdPartyOrderDTO.builder()
                        .orderId(order.getOrderId())
                        .clientOrderId(order.getClientOrderId())
                        .otcSymbolId(order.getOtcSymbolId())
                        .tokenId(order.getTokenId())
                        .currencyId(order.getCurrencyId())
                        .tokenAmount(order.getTokenAmount())
                        .currencyAmount(order.getCurrencyAmount())
                        .price(order.getPrice())
                        .side(order.getSide())
                        .orgId(order.getOrgId())
                        .userId(order.getUserId())
                        .accountId(order.getAccountId())
                        .status(order.getStatus())
                        .errorMessage(order.getErrorMessage())
                        .thirdPartyName(order.getThirdPartyName())
                        .created(order.getCreated())
                        .updated(order.getUpdated())
                        .build()
        ).collect(Collectors.toList());
    }


}
