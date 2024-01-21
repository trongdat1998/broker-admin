package io.bhex.broker.admin.service.impl;

import com.google.gson.Gson;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import io.bhex.broker.admin.controller.dto.OptionInfoDto;
import io.bhex.broker.admin.controller.param.OptionCreatePO;
import io.bhex.broker.admin.grpc.client.OptionClient;
import io.bhex.broker.admin.service.OptionService;
import io.bhex.broker.grpc.admin.CreateOptionReply;
import io.bhex.broker.grpc.admin.CreateOptionRequest;
import io.bhex.broker.grpc.admin.QueryOptionListRequest;
import io.bhex.broker.grpc.admin.QueryOptionListResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @ProjectName:
 * @Package:
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2020/9/9 上午10:29
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class OptionServiceImpl implements OptionService {

    @Resource
    private OptionClient optionClient;

    @Override
    public CreateOptionReply createOption(Long orgId, OptionCreatePO optionCreatePO) {
        CreateOptionReply reply = optionClient.createOption(structureReq(orgId, optionCreatePO));
        return reply;
    }

    @Override
    public List<OptionInfoDto> queryOptionList(QueryOptionListRequest request) {
        QueryOptionListResponse response = optionClient.queryOptionList(request);
        if (response.getInfoCount() == 0) {
            return new ArrayList<>();
        }
        List<OptionInfoDto> optionInfoDtoList = new ArrayList<>();
        response.getInfoList().forEach(info -> {
            optionInfoDtoList.add(OptionInfoDto.builder()
                    .id(info.getId())
                    .tokenId(info.getTokenId())
                    .tokenName(info.getTokenName())
                    .strikePrice(new BigDecimal(info.getStrikePrice()))
                    .issueDate(info.getIssueDate())
                    .settlementDate(info.getSettlementDate())
                    .isCall(info.getIsCall())
                    .maxPayOff(new BigDecimal(info.getMaxPayOff()))
                    .positionLimit(new BigDecimal(info.getPositionLimit()))
                    .coinToken(info.getCoinToken())
                    .indexToken(info.getIndexToken())
                    .minTradeAmount(new BigDecimal(info.getMinTradeAmount()))
                    .minPricePrecision(new BigDecimal(info.getMinPricePrecision()))
                    .minTradeQuantity(new BigDecimal(info.getMinTradeQuantity()))
                    .digitMergeList(info.getDigitMergeList())
                    .basePrecision(new BigDecimal(info.getBasePrecision()))
                    .quotePrecision(new BigDecimal(info.getQuotePrecision()))
                    .minPrecision(new BigDecimal(info.getMinPrecision()))
                    .orgId(info.getBrokerId())
                    .exchangeId(info.getExchangeId())
                    .takerFeeRate(new BigDecimal(info.getTakerFeeRate()))
                    .makerFeeRate(new BigDecimal(info.getMakerFeeRate()))
                    .underlyingId(info.getUnderlyingId())
                    .build());
        });
        return optionInfoDtoList;
    }

    private CreateOptionRequest structureReq(Long orgId, OptionCreatePO optionInfo) {
        log.info("OptionCreatePO info {}", new Gson().toJson(optionInfo));
        return CreateOptionRequest
                .newBuilder()
                .setId(optionInfo.getId() != null && optionInfo.getId() > 0 ? optionInfo.getId() : 0)
                .setBrokerId(orgId)
                .setTokenId(optionInfo.getTokenId())
                .setTokenName(optionInfo.getTokenName())
                .setStrikePrice(optionInfo.getStrikePrice().setScale(8, RoundingMode.DOWN).toPlainString())
                .setIssueDate(optionInfo.getIssueDate())
                .setSettlementDate(optionInfo.getSettlementDate())
                .setIsCall(optionInfo.getIsCall())
                .setMaxPayOff(optionInfo.getMaxPayOff().setScale(8, RoundingMode.DOWN).toPlainString())
                .setPositionLimit(String.valueOf(optionInfo.getPositionLimit()))
                .setCoinToken(optionInfo.getCoinToken())
                .setIndexToken(optionInfo.getIndexToken())
                .setMinTradeAmount(optionInfo.getMinTradeAmount().setScale(8, RoundingMode.DOWN).toPlainString())
                .setMinPricePrecision(optionInfo.getMinPricePrecision().setScale(8, RoundingMode.DOWN).toPlainString())
                .setMinTradeQuantity(optionInfo.getMinTradeQuantity().setScale(8, RoundingMode.DOWN).toPlainString())
                .setDigitMergeList(optionInfo.getDigitMergeList())
                .setBasePrecision(optionInfo.getBasePrecision().setScale(8, RoundingMode.DOWN).toPlainString())
                .setQuotePrecision(optionInfo.getQuotePrecision().setScale(8, RoundingMode.DOWN).toPlainString())
                .setMinPrecision(8)
                .setBrokerId(orgId)
                .setExchangeId(301)
                .setType(8)
                .setCategory(3)
                .setTakerFeeRate(optionInfo.getTakerFeeRate().setScale(8, RoundingMode.DOWN).toPlainString())
                .setMakerFeeRate(optionInfo.getMakerFeeRate().setScale(8, RoundingMode.DOWN).toPlainString())
                .setUnderlyingId(optionInfo.getUnderlyingId())
                .build();
    }
}
