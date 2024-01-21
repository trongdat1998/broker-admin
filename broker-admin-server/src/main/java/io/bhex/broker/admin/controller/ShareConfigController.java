package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.ShareConfigDTO;
import io.bhex.broker.admin.controller.param.ShareConfigPO;
import io.bhex.broker.admin.service.ShareConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 2019/7/1 2:34 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/share_config")
public class ShareConfigController extends BaseController {

    @Autowired
    private ShareConfigService shareConfigService;

    @RequestMapping(value = "/query")
    public ResultModel query() {
        ShareConfigDTO shareConfigInfo = shareConfigService.getShareConfigInfo(getOrgId());
        return ResultModel.ok(shareConfigInfo);
    }

    @RequestMapping(value = "/save")
    public ResultModel save(@RequestBody @Valid ShareConfigPO param) {
        param.setBrokerId(getOrgId());
        param.setAdminUserId(getRequestUserId());
        Boolean isOk = shareConfigService.saveShareConfigInfo(param);
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }
}
