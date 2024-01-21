package io.bhex.broker.admin.controller;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.OptionInfoDto;
import io.bhex.broker.admin.controller.param.OptionCreatePO;
import io.bhex.broker.admin.controller.param.SimpleOptionOrderPO;
import io.bhex.broker.admin.service.OptionService;
import io.bhex.broker.grpc.admin.QueryOptionListRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2019-01-31 17:20
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/option")
public class OptionController extends BrokerBaseController {

    @Resource
    private OptionService optionService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultModel<Integer> createNewOption(@RequestBody @Valid OptionCreatePO createPO) {
        Long orgId = getOrgId();
//        CreateOptionRequest request = CreateOptionRequest.getDefaultInstance();
//        BeanUtils.copyProperties(createPO, request);
//        request.toBuilder().setBrokerId(orgId).build();
        try {
            optionService.createOption(orgId, createPO);
            return ResultModel.ok();
        } catch (Exception ex) {
            log.info("createNewOption error {}", ex);
            return ResultModel.ok();
        }
    }

    @RequestMapping(value = "/current_options", method = RequestMethod.POST)
    public ResultModel<List<OptionInfoDto>> queryCurrentOrders(@RequestBody @Valid SimpleOptionOrderPO po) {
        List<OptionInfoDto> list = new ArrayList<>();
        try {
            list = optionService
                    .queryOptionList(QueryOptionListRequest
                            .newBuilder()
                            .setOrgId(getOrgId())
                            .setFromId(po.getFromId() != null && po.getFromId() > 0 ? po.getFromId() : 0)
                            .setLimit(po.getPageSize() != null && po.getPageSize() > 0 ? po.getPageSize() : 20)
                            .build());
        } catch (Exception ex) {
            log.info("queryCurrentOrders error {} ", ex);
        }
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(list);
    }
}
