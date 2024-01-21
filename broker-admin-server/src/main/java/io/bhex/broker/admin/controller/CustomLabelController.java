package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.CustomLabelDTO;
import io.bhex.broker.admin.controller.dto.SaveCustomLabelDTO;
import io.bhex.broker.admin.controller.param.DelCustomLabelPO;
import io.bhex.broker.admin.controller.param.QueryCustomLabelPO;
import io.bhex.broker.admin.controller.param.SaveCustomLabelPO;
import io.bhex.broker.admin.service.CustomLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 2019/12/12 9:04 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1/custom_label")
public class CustomLabelController extends BaseController {

    @Autowired
    private CustomLabelService customLabeService;

    @RequestMapping(value = "/query")
    public ResultModel queryCustomLabel(@RequestBody @Valid QueryCustomLabelPO po) {
        po.setOrgId(getOrgId());
        po.setType(1);
        List<CustomLabelDTO> labelDTOList = customLabeService.queryCustomLabel(po);
        return ResultModel.ok(labelDTOList);
    }

    @RequestMapping(value = "/del")
    public ResultModel delCustomLabel(@RequestBody @Valid DelCustomLabelPO po) {
        po.setOrgId(getOrgId());
        Boolean isOk = customLabeService.delCustomLabel(po);
        return ResultModel.ok(isOk);
    }

    @RequestMapping(value = "/save")
    public ResultModel saveCustomLabel(@RequestBody @Valid SaveCustomLabelPO po) {
        po.setOrgId(getOrgId());
        po.setType(1);
        SaveCustomLabelDTO dto = customLabeService.saveCustomLabel(po);

        if (dto.getRet() != 0) {
            ErrorCode errorCode = ErrorCode.valueOF(dto.getRet());
            if (Objects.nonNull(errorCode)) {
                return ResultModel.error(errorCode.getCode(), errorCode.getDesc(), dto);
            }
        }
        return ResultModel.ok(dto);
    }

}
