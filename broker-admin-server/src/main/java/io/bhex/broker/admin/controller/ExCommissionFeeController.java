package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.proto.ErrorCode;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BrokerTradeMinFeeDTO;
import io.bhex.broker.admin.controller.dto.ExCommissionFeeDTO;
import io.bhex.broker.admin.controller.dto.ExFeeConfigDTO;
import io.bhex.broker.admin.controller.param.BrokerTradeMinFeePO;
import io.bhex.broker.admin.controller.param.ExCommissionFeePO;
import io.bhex.broker.admin.grpc.client.impl.OrgClient;
import io.bhex.broker.admin.service.impl.BrokerTradeMinFeeService;
import io.bhex.broker.admin.service.impl.ExCommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @ProjectName: exchange
 * @Package: io.bhex.ex.admin.controller
 * @Author: ming.xu
 * @CreateDate: 16/11/2018 10:21 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1/platform_account/ex_fee")
public class ExCommissionFeeController extends BaseController {

    @Autowired
    private ExCommissionService exCommissionService;

    @Autowired
    private BrokerTradeMinFeeService brokerTradeMinFeeService;
    @Autowired
    private OrgClient orgClient;

    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public ResultModel getFeeConfig(AdminUserReply adminUser) {
        Long orgId = orgClient.findTrustExchangeByBrokerId(adminUser.getOrgId()).getExchangeId();
        ExCommissionFeeDTO exCommissionFee = exCommissionService.getExCommissionFee(orgId, ExCommissionService.COMMISSION_TYPE_COIN);
        BrokerTradeMinFeeDTO brokerTradeMinFee = brokerTradeMinFeeService.getBrokerTradeMinFee(orgId);
        ExFeeConfigDTO dto = ExFeeConfigDTO.builder()
                .brokerTradeMinFee(brokerTradeMinFee)
                .exCommissionFee(exCommissionFee)
                .build();
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/update_commission", method = RequestMethod.POST)
    public ResultModel updateCommission(@RequestBody @Valid ExCommissionFeePO param, AdminUserReply adminUser) {
        Long orgId = orgClient.findTrustExchangeByBrokerId(adminUser.getOrgId()).getExchangeId();
        param.setExchangeId(orgId);
        int code = exCommissionService.updateExcommissionFee(param, ExCommissionService.COMMISSION_TYPE_COIN);
        if(code == ErrorCode.ERR_Ineligible_Commission_fee.getNumber()){
            return ResultModel.error("commission.rate.less.than.match.exchange");
        }
        if(code != ErrorCode.SUCCESS_VALUE){
            return ResultModel.error("internal.error");
        }
//        if (isOk) {
//            return ResultModel.ok(isOk);
//        } else {
//            return ResultModel.error("internal.error");
//        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/broker_trade_fee/update_min", method = RequestMethod.POST)
    public ResultModel updateBrokerTradeMinFee(@RequestBody @Valid BrokerTradeMinFeePO param, AdminUserReply adminUser) {
        Long orgId = orgClient.findTrustExchangeByBrokerId(adminUser.getOrgId()).getExchangeId();
        param.setExchangeId(orgId);
        int code = brokerTradeMinFeeService.updateBrokerTradeMinfee(param);
//        ERR_Ineligible_Maker_Fee_Rate = 2002; //ineligible.maker.fee.rate  maker.fee.greater.than.broker
//        ERR_Ineligible_Taker_Fee_Rate = 2003; //ineligible.taker.fee.rate  taker.fee.greater.than.broker
//        ERR_Ineligible_Maker_Bonus_Rate = 2004; //ineligible.maker.bonus.rate  maker.bonus.rate.greater.than.broker
        if(code == ErrorCode.ERR_Ineligible_Maker_Fee_Rate.getNumber()){
            return ResultModel.error("maker.fee.greater.than.broker");
        }
        if(code == ErrorCode.ERR_Ineligible_Taker_Fee_Rate.getNumber()){
            return ResultModel.error("taker.fee.greater.than.broker");
        }
        if(code == ErrorCode.ERR_Ineligible_Maker_Bonus_Rate.getNumber()){
            return ResultModel.error("maker.bonus.rate.less.than.broker");
        }
        if(code != ErrorCode.SUCCESS_VALUE){
            return ResultModel.error("internal.error");
        }
        return ResultModel.ok();
    }


}
