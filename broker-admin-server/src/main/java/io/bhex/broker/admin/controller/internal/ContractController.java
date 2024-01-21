package io.bhex.broker.admin.controller.internal;

import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.param.ChangeContractPO;
import io.bhex.broker.admin.controller.param.ExchangeContractPO;
import io.bhex.broker.admin.service.ExchangeContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.internal
 * @Author: ming.xu
 * @CreateDate: 19/09/2018 11:36 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
//@RestController
//@RequestMapping("/api/v1/internal/contract")
public class ContractController extends BaseController {

//    @Autowired
//    private ExchangeContractService exchangeContractService;
//
//    @RequestMapping(value = "/enable", method = RequestMethod.POST)
//    public ResultModel enableApplication(@RequestBody ChangeContractPO po) {
//        Boolean isOk = exchangeContractService.enableApplication(po.getBrokerId(), po.getContractId(), po.getExchangeId(), 0L, false);
//        if (isOk) {
//            return ResultModel.ok(isOk);
//        } else {
//            return ResultModel.error("internal.error");
//        }
//    }
//
//    @RequestMapping(value = "/reject", method = RequestMethod.POST)
//    public ResultModel rejectApplication(@RequestBody ChangeContractPO po) {
//        Boolean isOk = exchangeContractService.rejectApplication(po.getBrokerId(), po.getContractId(), po.getExchangeId(), 0L, false);
//        if (isOk) {
//            return ResultModel.ok(isOk);
//        } else {
//            return ResultModel.error("internal.error");
//        }
//    }
//
//    @RequestMapping(value = "/reopen", method = RequestMethod.POST)
//    public ResultModel enableContract(@RequestBody ChangeContractPO po) {
//        Boolean isOk = exchangeContractService.otherReopenContract(po.getBrokerId(), po.getContractId());
//        if (isOk) {
//            return ResultModel.ok(isOk);
//        } else {
//            return ResultModel.error("internal.error");
//        }
//    }
//
//    @RequestMapping(value = "/close", method = RequestMethod.POST)
//    public ResultModel rejectContract(@RequestBody ChangeContractPO po) {
//        Boolean isOk = exchangeContractService.closeExchangeContract(po.getBrokerId(), po.getContractId(), po.getExchangeId(), 0L, false);
//        if (isOk) {
//            return ResultModel.ok(isOk);
//        } else {
//            return ResultModel.error("internal.error");
//        }
//    }
//
//    @RequestMapping(method = RequestMethod.POST)
//    public ResultModel addApplication(@RequestBody ExchangeContractPO param) {
//        Boolean isOk = exchangeContractService.addApplication(param);
//        if (isOk) {
//            return ResultModel.ok(isOk);
//        } else {
//            return ResultModel.error("internal.error");
//        }
//    }
}
