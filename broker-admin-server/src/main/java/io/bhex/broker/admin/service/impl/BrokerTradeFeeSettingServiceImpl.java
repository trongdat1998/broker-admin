package io.bhex.broker.admin.service.impl;

import io.bhex.base.account.GetBrokerTradeMinFeeRequest;
import io.bhex.base.account.GetBrokerTradeMinFeeResponse;
import io.bhex.base.admin.common.BrokerTradeFeeRateReply;
import io.bhex.base.admin.common.UpdateBrokerTradeFeeReply;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.controller.dto.BrokerTradeMinFeeDTO;
import io.bhex.broker.admin.controller.param.BrokerTradeFeeRes;
import io.bhex.broker.admin.controller.param.UpdateBrokerTradeFeePO;
import io.bhex.broker.admin.grpc.client.BorkerTradeFeeSettingClient;
import io.bhex.broker.admin.service.BrokerTradeFeeSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Description:
 * @Date: 2018/9/28 上午11:30
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@Service
public class BrokerTradeFeeSettingServiceImpl implements BrokerTradeFeeSettingService {


    @Autowired
    private BorkerTradeFeeSettingClient brokerTradeFeeSettingClient;

    @Override
    public void addTradeFeeIfNotExisted(Long brokerId, Long exchangeId, String symbolId, Integer category){
        symbolId = symbolId.replace("/" , "");
        BrokerTradeFeeRateReply reply = brokerTradeFeeSettingClient
                .getLatestBrokerTradeFeeSetting(brokerId, exchangeId, symbolId);
        log.info("getLatestBrokerTradeFeeSetting symbolId:{} reply:{}", symbolId, reply);
        if (reply.getExchangeId() == 0) { //如果没有则添加
            addDefaultTradeFee(brokerId, exchangeId, symbolId, category);
            log.info("init fee rate {} {} {}", brokerId, exchangeId, symbolId);
        }
    }

    @Override
    public BrokerTradeFeeRes addDefaultTradeFee(Long brokerId, Long exchangeId, String symbolId, Integer category) {
         symbolId = symbolId.replace("/" , "");
        BrokerTradeFeeRes res = BrokerTradeFeeRes.defaultInstance(symbolId);
         // 如果为期货，默认值为0.0002，taker 0.00075
        if (Objects.nonNull(category) && category == 4) {
            res.setMakerFeeRate(new BigDecimal("0.0002"));
            res.setTakerFeeRate(new BigDecimal("0.00075"));
         } else {
            BrokerTradeMinFeeDTO minFeeDTO = getBrokerTradeMinFee(exchangeId, brokerId);
            res.setMakerFeeRate(minFeeDTO.getMakerFeeRate());
            res.setTakerFeeRate(minFeeDTO.getTakerFeeRate());
         }
        editTradeFee(brokerId, exchangeId, symbolId,
                res.getMakerFeeRate(), res.getTakerFeeRate(), BigDecimal.ZERO, category);
         return res;
    }

    @Override
    public Combo2<Boolean, String> editTradeFee(Long brokerId, Long exchangeId, String symbolId, BigDecimal makerFeeRate, BigDecimal takerFeeRate, BigDecimal takerRewardToMakerRate, Integer category) {

        if (Objects.nonNull(category) && category == 4) {
            if (takerFeeRate.compareTo(BigDecimal.ZERO) < 0) {
                return new Combo2<>(false, "futures.takerFeeRate.too.low");
            }
        }
        UpdateBrokerTradeFeePO addPo = new UpdateBrokerTradeFeePO();
        addPo.setExchangeId(exchangeId);
        addPo.setMakerFeeRate(makerFeeRate);
        addPo.setTakerFeeRate(takerFeeRate);
        addPo.setTakerRewardToMakerRate(takerRewardToMakerRate);
        addPo.setSymbolId(symbolId);
        UpdateBrokerTradeFeeReply reply = brokerTradeFeeSettingClient.updateBrokerTradeFee(brokerId, addPo);
        log.info("editTradeFee brokerId={} exchangeId={} symbolId={}, result={}", brokerId, exchangeId, symbolId, reply);
        if(reply.getResult() == false){
            return new Combo2<>(false, reply.getMessage());
        }

        return new Combo2<>(true, null);
    }

    @Override
    public BrokerTradeFeeRateReply getLatestBrokerTradeFeeSetting(Long brokerId, Long exchangeId, String symbolId) {
        return brokerTradeFeeSettingClient.getLatestBrokerTradeFeeSetting(brokerId, exchangeId, symbolId);
    }

    @Override
    public BrokerTradeMinFeeDTO getBrokerTradeMinFee(Long exchangeId, Long orgId) {
        GetBrokerTradeMinFeeRequest request = GetBrokerTradeMinFeeRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setExchangeId(exchangeId).build();
        GetBrokerTradeMinFeeResponse response = brokerTradeFeeSettingClient.getBrokerTradeMinFee(request);

        BrokerTradeMinFeeDTO dto = BrokerTradeMinFeeDTO.builder()
                .makerBonusRate(DecimalUtil.toBigDecimal(response.getMakerBonusRate()))
                .makerFeeRate(DecimalUtil.toBigDecimal(response.getMakerFeeRate()))
                .takerFeeRate(DecimalUtil.toBigDecimal(response.getTakerFeeRate()))
                .build();
        return dto;
    }
}
