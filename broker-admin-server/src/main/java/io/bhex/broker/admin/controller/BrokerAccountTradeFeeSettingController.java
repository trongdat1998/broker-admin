package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.BrokerTradeFeeRateReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.controller.dto.BrokerAccountTradeFeeGroupDTO;
import io.bhex.broker.admin.controller.dto.BrokerTradeMinFeeDTO;
import io.bhex.broker.admin.controller.dto.ContractExchangeInfo;
import io.bhex.broker.admin.controller.param.BrokerAccountTradeFeeGroupPO;
import io.bhex.broker.admin.controller.param.QueryBrokerAccountTradeFeeGroupIdPO;
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.service.BrokerAccountTradeFeeSettingService;
import io.bhex.broker.admin.service.BrokerTradeFeeSettingService;
import io.bhex.broker.admin.service.ExchangeContractService;
import io.bhex.broker.grpc.admin.QuerySymbolReply;
import io.bhex.broker.grpc.admin.SymbolDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: trade fee setting
 * @Date: 2018/9/4 下午8:17
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/account_trade_fee_setting")
public class BrokerAccountTradeFeeSettingController extends BaseController {


    @Autowired
    private BrokerAccountTradeFeeSettingService feeService;
    @Autowired
    private ExchangeContractService exchangeContractService;
    @Autowired
    private SymbolClient symbolClient;
    @Autowired
    private BrokerTradeFeeSettingService brokerTradeFeeSettingService;

    public static final int PAGE_SIZE = 100;

    @BussinessLogAnnotation(opContent = "Edit Discount Configuration Group:{#po.groupName} AccountIds:{#po.accountIds} "
            + "MakerDiscountRatio:{#po.makerFeeRateAdjust} TakerDiscountRatio:{#po.takerFeeRateAdjust}")
    @RequestMapping(value = "/edit_group")
    public ResultModel<Void> editGroup(@RequestBody @Valid BrokerAccountTradeFeeGroupPO po) {
        Long brokerId = getOrgId();
        List<ContractExchangeInfo> contractExchangeInfos = exchangeContractService.listALlExchangeContractInfo(brokerId);
        if (CollectionUtils.isEmpty(contractExchangeInfos)) {
            return ResultModel.error("account.trade.fee.add.exchange");
        }

        List<Long> exchangeIds = contractExchangeInfos.stream().map(info -> info.getExchangeId()).collect(Collectors.toList());
        for (Long exchangeId : exchangeIds) {
            for (int i = 1; i < 100; i++) {
                 QuerySymbolReply symbolReply = symbolClient.querySymbol(i,
                         PAGE_SIZE, 1, null, "", brokerId, exchangeId, null);
                List<SymbolDetail> symbolDetails = symbolReply.getSymbolDetailsList();
                if (CollectionUtils.isEmpty(symbolDetails)) {
                    break;
                }
                for (SymbolDetail detail : symbolDetails) {
                    BrokerTradeFeeRateReply feeRateReply = brokerTradeFeeSettingService
                            .getLatestBrokerTradeFeeSetting(brokerId, exchangeId, detail.getSymbolId());
                    log.info("symbol:{} takerFeeRate:{}", detail.getSymbolId(), feeRateReply.getTakerFeeRate());
                    if (StringUtils.isEmpty(feeRateReply.getTakerFeeRate())) {
                        continue;
                    }
                    if (new BigDecimal(feeRateReply.getTakerFeeRate()).multiply(po.getTakerFeeRateAdjust()).compareTo(BizConstant.ADJUST_MIN_TRADE_FEE_RATE) < 0) {
                        return ResultModel.error("takerFeeRate.too.low");
                    }
                }
                if (PAGE_SIZE * i >= symbolReply.getTotal()) {
                    break;
                }
            }
        }


//        for (Long exchangeId : exchangeIds) {
//            BrokerTradeMinFeeDTO dto = brokerTradeFeeSettingService.getBrokerTradeMinFee(exchangeId);
//            if (dto.getTakerFeeRate().multiply(po.getTakerFeeRateAdjust()).compareTo(new BigDecimal("0.0001")) < 0) {
//                return ResultModel.error("takerFeeRate.too.low");
//            }
//        }


        BigDecimal minMakerBonusRate = feeService.getMinMakerBonusRate(brokerId, exchangeIds);
        if (po.getTakerRewardToMakerRateAdjust().compareTo(minMakerBonusRate) > 0) {
            return ResultModel.error("takerRewardToMakerRate.more.than.exchange");
        }

        ResultModel resultModel = feeService.editBrokerAccountTradeFeeGroup(brokerId, po);
        return resultModel;
    }


    @RequestMapping(value = "/list")
    public ResultModel list() {
        List<BrokerAccountTradeFeeGroupDTO> list = feeService.getBrokerAccountTradeFeeGroups(getOrgId());
        return ResultModel.ok(list);
    }


    @RequestMapping(value = "/detail")
    public ResultModel showDetail(@RequestBody @Valid QueryBrokerAccountTradeFeeGroupIdPO po) {
        BrokerAccountTradeFeeGroupDTO dto = feeService.getBrokerAccountTradeFeeGroup(getOrgId(), po.getGroupId());
        return ResultModel.ok(dto);
    }

    @BussinessLogAnnotation(opContent = "Enable Discount Group groupId:{#po.groupId}")
    @RequestMapping(value = "/enable")
    public ResultModel enableDiscountGroup(@RequestBody @Valid QueryBrokerAccountTradeFeeGroupIdPO po) {
        feeService.enableBrokerAccountTradeFeeGroup(getOrgId(), po.getGroupId());
        return ResultModel.ok();
    }

    @BussinessLogAnnotation(opContent = "Disable Discount Group groupId:{#po.groupId}")
    @RequestMapping(value = "/disable")
    public ResultModel disableDiscountGroup(@RequestBody @Valid QueryBrokerAccountTradeFeeGroupIdPO po) {
        feeService.disableBrokerAccountTradeFeeGroup(getOrgId(), po.getGroupId());
        return ResultModel.ok();
    }


    private boolean checkPrecious(BigDecimal val) {
        BigDecimal c = val.multiply(BizConstant.TRADE_FEE_RATE_PRECISION);
        return c.subtract(c.setScale(0, BigDecimal.ROUND_DOWN)).compareTo(BigDecimal.ZERO) > 0;
    }
}
