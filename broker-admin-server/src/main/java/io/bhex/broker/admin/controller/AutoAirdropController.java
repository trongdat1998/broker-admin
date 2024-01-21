package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.AutoAirdropDTO;
import io.bhex.broker.admin.controller.param.AutoAirdropPO;
import io.bhex.broker.admin.service.AutoAirdropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 30/11/2018 3:50 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping({"/api/v1/airdrop"})
public class AutoAirdropController extends BaseController {

    @Autowired
    private AutoAirdropService autoAirdropService;

    @RequestMapping(value = "/auto_airdrop", method = RequestMethod.POST)
    @BussinessLogAnnotation(opContent = "Create {#param.airdropType == 1 ? 'Registered User' : ''} Airdrop AirdropToken:{param.tokenId} TokenNum:{param.airdropTokenNum}")
    public ResultModel createAutoAirdrop(@RequestBody @Valid AutoAirdropPO param) {
        Long brokerId = getOrgId();
        param.setBrokerId(brokerId);
        // 目前只支持注册空投，所以类型为 1
        param.setAirdropType(1);
        // 目前只支持从运营账户空投，所以类型为 1
        param.setAccountType(1);
        Boolean isOk = autoAirdropService.saveAutoAirdrop(param);
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }

    @RequestMapping(value = "/show_auto_airdrop", method = RequestMethod.POST)
    public ResultModel showAutoAirdrop() {
        Long brokerId = getOrgId();
        AutoAirdropDTO autoAirdrop = autoAirdropService.getAutoAirdrop(brokerId);
        return ResultModel.ok(autoAirdrop);
    }
}
