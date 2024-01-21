package io.bhex.broker.admin.controller;


import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.admin.common.BrokerTradeFeeRateReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.controller.dto.BrokerTradeMinFeeDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.service.BrokerTradeFeeSettingService;
import io.bhex.broker.grpc.admin.QuerySymbolReply;
import io.bhex.broker.grpc.admin.SymbolDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: trade fee setting
 * @Date: 2018/9/4 下午8:17
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/trade_fee_setting")
public class BrokerTradeFeeSettingController extends BaseController {

    @Autowired
    private SymbolClient symbolClient;

    @Autowired
    private BrokerTradeFeeSettingService brokerTradeFeeSettingService;

    //从brokerserver拿到券商对应的交易所币对，然后再取费率设置信息
    @RequestMapping(value = "/list")
    public ResultModel<PaginationVO> list(@RequestBody TradeFeeListPO po) {
        String symbolName = StringUtils.isAnyEmpty(po.getQuoteTokenId(), po.getBaseTokenId())
                ? null
                : po.getBaseTokenId() + po.getQuoteTokenId();

        AdminUserReply adminUserReply = getRequestUser();
        QuerySymbolReply symbolReply = symbolClient.querySymbol(po.getCurrent(),
                po.getPageSize(), po.getCategory(), null, symbolName, adminUserReply.getOrgId(), po.getExchangeId(), null);
        PaginationVO<BrokerTradeFeeRes> vo = new PaginationVO();
        BeanUtils.copyProperties(symbolReply, vo);

        List<BrokerTradeFeeRes> dtos = new ArrayList<>();
        List<SymbolDetail> symbols = symbolReply.getSymbolDetailsList();
        for (SymbolDetail detail : symbols) {
            BrokerTradeFeeRes dto;
            BrokerTradeFeeRateReply feeRateReply = brokerTradeFeeSettingService
                    .getLatestBrokerTradeFeeSetting(adminUserReply.getOrgId(), detail.getExchangeId(), detail.getSymbolId());
            if (feeRateReply.getExchangeId() != 0) {
                dto = BrokerTradeFeeRes.parseFrom(feeRateReply);
            } else { //如果不存在费率设置 默认为0.1
                dto = BrokerTradeFeeRes.defaultInstance(detail.getSymbolId());
                dto.setExchangeId(detail.getExchangeId());
                dto = brokerTradeFeeSettingService.addDefaultTradeFee(adminUserReply.getOrgId(),
                        detail.getExchangeId(), detail.getSymbolId(), po.getCategory());
            }
            // 如果为币币 则处理如下
            if (po.getCategory() == 1) {
                dto.setSymbolId(detail.getBaseTokenId() + "/" + detail.getQuoteTokenId());
            } else {
                dto.setSymbolId(detail.getSymbolId());
            }
            dtos.add(dto);
            log.info("symbol bh:{} dto:{}", detail.getSymbolId(), dto.getSymbolId());
        }
        vo.setList(dtos);
        return ResultModel.ok(vo);
    }


    @BussinessLogAnnotation(opContent = "Edit Trade Fee symbolId:{#po.symbolId} makerFeeRate:{#po.makerFeeRate} takerFeeRate:{#po.takerFeeRate}")
    @RequestMapping(value = "/edit_trade_fee")
    public ResultModel<Void> editTradeFee(@RequestBody @Valid UpdateBrokerTradeFeePO po) {
        if (checkPrecious(po.getMakerFeeRate())) {
            return ResultModel.validateFail("precision.error");
        }
        if (checkPrecious(po.getTakerFeeRate())) {
            return ResultModel.validateFail("precision.error");
        }
        if (checkPrecious(po.getTakerRewardToMakerRate())) {
            return ResultModel.validateFail("precision.error");
        }

        AdminUserReply adminUserReply = getRequestUser();
        po.setSymbolId(po.getSymbolId().replace("/", ""));
        Combo2<Boolean, String> combo2 = brokerTradeFeeSettingService
                .editTradeFee(adminUserReply.getOrgId(), po.getExchangeId(),
                        po.getSymbolId(), po.getMakerFeeRate(), po.getTakerFeeRate(), po.getTakerRewardToMakerRate(), po.getCategory());
        if (!combo2.getV1()) {
            return ResultModel.error(combo2.getV2());
        }

        return ResultModel.ok();
    }

    @RequestMapping(value = "/trade_min_fee")
    public ResultModel<Void> getBrokerTradeMinFee(@RequestBody @Valid ExchangeIdPO po) {
        BrokerTradeMinFeeDTO dto = brokerTradeFeeSettingService.getBrokerTradeMinFee(po.getExchangeId(), getOrgId());
        return ResultModel.ok(dto);
    }

    private boolean checkPrecious(BigDecimal val) {
        BigDecimal c = val.multiply(BizConstant.TRADE_FEE_RATE_PRECISION);
        return c.subtract(c.setScale(0, BigDecimal.ROUND_DOWN)).compareTo(BigDecimal.ZERO) > 0;
    }
}
