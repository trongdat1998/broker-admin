package io.bhex.broker.admin.controller;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.validation.Valid;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.MatchDTO;
import io.bhex.broker.admin.controller.dto.OrderDTO;
import io.bhex.broker.admin.controller.dto.PositionDto;
import io.bhex.broker.admin.controller.dto.SettlementDto;
import io.bhex.broker.admin.controller.param.SimpleOptionOrderPO;
import io.bhex.broker.admin.service.OptionOrderService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2019-01-31 17:20
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/option/order")
public class OptionOrderController extends BrokerBaseController {

    @Resource
    private OptionOrderService optionOrderService;

    @RequestMapping(value = "/current_orders", method = RequestMethod.POST)
    public ResultModel<List<OrderDTO>> queryCurrentOrders(@RequestBody @Valid SimpleOptionOrderPO po) {
        if (Objects.isNull(po.getUserId()) || po.getUserId() == 0L) {
            return ResultModel.ok(Arrays.asList());
        }
        List<OrderDTO> list = optionOrderService
                .queryCurrentOrders(getOrgId(), po.getUserId(),
                        null, po.getFromId(),
                        po.getLastId(), po.getStartTime(),
                        po.getEndTime(), null,
                        null, null,
                        null, po.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/history_orders", method = RequestMethod.POST)
    public ResultModel<List<OrderDTO>> queryHistoryOrders(@RequestBody @Valid SimpleOptionOrderPO po) {
        if (Objects.isNull(po.getUserId()) || po.getUserId() == 0L) {
            return ResultModel.ok(Arrays.asList());
        }
        List<OrderDTO> list = optionOrderService
                .queryHistoryOrders(getOrgId(), po.getUserId(),
                        null, po.getFromId(),
                        po.getLastId(), po.getStartTime(),
                        po.getEndTime(), null,
                        null, null,
                        null, po.getPageSize(), null);
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/match_info", method = RequestMethod.POST)
    public ResultModel<List<MatchDTO>> queryMatchInfo(@RequestBody @Valid SimpleOptionOrderPO po) {
        if (Objects.isNull(po.getUserId()) || po.getUserId() == 0L) {
            return ResultModel.ok(Arrays.asList());
        }
        List<MatchDTO> list = optionOrderService
                .queryMatchInfo(getOrgId(), po.getUserId(),
                        null, po.getFromId(),
                        po.getLastId(), po.getStartTime(),
                        po.getEndTime(), po.getPageSize(),
                        null);
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/option_positions", method = RequestMethod.POST)
    public ResultModel<List<PositionDto>> getOptionPositions(@RequestBody @Valid SimpleOptionOrderPO po) {
        if (Objects.isNull(po.getUserId()) || po.getUserId() == 0L) {
            return ResultModel.ok(Arrays.asList());
        }
        List<PositionDto> list = optionOrderService
                .getOptionPositions(getOrgId(), po.getUserId(),
                        null, null,
                        po.getFromId(), po.getLastId(),
                        po.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/option_settlements", method = RequestMethod.POST)
    public ResultModel<List<SettlementDto>> getOptionSettlement(@RequestBody @Valid SimpleOptionOrderPO po) {
        if (Objects.isNull(po.getUserId()) || po.getUserId() == 0L) {
            return ResultModel.ok(Arrays.asList());
        }
        List<SettlementDto> list = optionOrderService
                .getOptionSettlement(getOrgId(), po.getUserId(),
                        null, po.getFromId(),
                        po.getLastId(), po.getStartTime(),
                        po.getEndTime(), po.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(list);
    }
}
