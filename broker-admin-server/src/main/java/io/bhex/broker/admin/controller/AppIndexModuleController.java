package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.AppIndexModuleDTO;
import io.bhex.broker.admin.service.AppConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description:
 * @Date: 2019/10/11 下午4:25
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/broker/config")
public class AppIndexModuleController  extends BaseController {

    @Autowired
    private AppConfigService appConfigService;

    @RequestMapping(value = "/app_index_modules")
    public ResultModel queryModules(@RequestParam(value = "moduleType") Integer moduleType) {
        return ResultModel.ok(appConfigService.queryModules(getOrgId(), moduleType));
    }

    @BussinessLogAnnotation(opContent = "Edit App Module")
    @RequestMapping(value = "/edit_app_index__module", method = RequestMethod.POST)
    public ResultModel editModule(@RequestBody @Valid AppIndexModuleDTO po) {
        return ResultModel.ok(appConfigService.editModule(getOrgId(), po));
    }
}
