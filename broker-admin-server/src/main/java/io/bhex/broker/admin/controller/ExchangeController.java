package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.ContractExchangeInfo;
import io.bhex.broker.admin.service.ExchangeContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 11:03 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping(value = "/api/v1/exchange")
public class ExchangeController extends BaseController {

    @Autowired
    private ExchangeContractService exchangeContractService;

    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultModel listExchange() {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        List<ContractExchangeInfo> contractExchangeInfos = exchangeContractService.listALlExchangeContractInfo(brokerId);
        return ResultModel.ok(contractExchangeInfos);
    }
}
