package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.DepositDTO;
import io.bhex.broker.admin.controller.param.DepositOrderListPO;
import io.bhex.broker.admin.controller.param.DepositReceiptExecPO;
import io.bhex.broker.admin.controller.param.GetBrokerUserPO;
import io.bhex.broker.admin.service.DepositService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 20/11/2018 6:04 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/deposit_order")
public class DepositController extends BrokerBaseController {

    @Autowired
    private DepositService depositService;

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ResultModel queryWithdrawOrder(@RequestBody @Valid DepositOrderListPO po) {
        log.info("request info : {}", po);
        AdminUserReply requestUser = getRequestUser();
        Long userId = null;

        long orgId = requestUser.getOrgId();
        // get userId id
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(getGetBrokerUserPO(po), orgId);
        if (combo2 != null) {
            userId = combo2.getV1();
        }

        if (null == userId) {
            return ResultModel.ok(new ArrayList());
        }

        List<DepositDTO> list = depositService.queryDepositOrders(orgId, userId,
                po.getNext() ? 0 : po.getFromId(), po.getNext() ? po.getLastId() : 0,
                po.getPageSize(), po.getTokenId(), po.getStartTime(), po.getEndTime(), po.getAddress(), po.getTxId());
        log.info("userId:{} list:{}", userId, list);

        return ResultModel.ok(list);
    }

    private GetBrokerUserPO getGetBrokerUserPO(DepositOrderListPO param) {
        GetBrokerUserPO po = new GetBrokerUserPO();
        po.setEmail(param.getEmail());
        po.setPhone(param.getMobile());
        po.setNationalCode(param.getNationalCode());
        po.setUserId(param.getUserId());
        return po;
    }
}
