package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.ExchangeContractDTO;
import io.bhex.broker.admin.controller.param.ChangeContractPO;
import io.bhex.broker.admin.controller.param.ExchangeContractPO;
import io.bhex.broker.admin.controller.param.PageRequestPO;
import io.bhex.broker.admin.service.ExchangeContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 31/08/2018 11:11 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1/contract")
public class ExchangeContractController extends BaseController {

    @Autowired
    private ExchangeContractService exchangeContractService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResultModel listContract(@RequestBody PageRequestPO page) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        PaginationVO<ExchangeContractDTO> vo = exchangeContractService.listExchangeContract(brokerId, page.getCurrent(), page.getPageSize());
        return ResultModel.ok(vo);
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/reopen", method = RequestMethod.POST)
    public ResultModel enableContract(@RequestBody ChangeContractPO po) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        Boolean isOk = exchangeContractService.reopenExchangeContract(brokerId, po.getContractId(), po.getExchangeId(), requestUser.getId());
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public ResultModel rejectContract(@RequestBody ChangeContractPO po) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        Boolean isOk = exchangeContractService.closeExchangeContract(brokerId, po.getContractId(), po.getExchangeId(), requestUser.getId());
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/contact_info", method = RequestMethod.POST)
    public ResultModel editContactInfo(@RequestBody ExchangeContractPO param) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        param.setAdminUserId(requestUser.getId());
        param.setBrokerId(brokerId);
        Boolean isOk = exchangeContractService.editContactInfo(param);
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }

    @RequestMapping(value = "/application/list", method = RequestMethod.POST)
    public ResultModel listApplication(@RequestBody PageRequestPO page) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        PaginationVO<ExchangeContractDTO> vo = exchangeContractService.listApplication(brokerId, page.getCurrent(), page.getPageSize());
        return ResultModel.ok(vo);
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/enable", method = RequestMethod.POST)
    public ResultModel enableApplication(@RequestBody ChangeContractPO po) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        Boolean isOk = exchangeContractService.enableApplication(brokerId, po.getContractId(), po.getExchangeId(), requestUser.getId());
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public ResultModel rejectApplication(@RequestBody ChangeContractPO po) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        Boolean isOk = exchangeContractService.rejectApplication(brokerId, po.getContractId(), po.getExchangeId(), requestUser.getId());
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }
}
