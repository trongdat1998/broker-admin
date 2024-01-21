package io.bhex.broker.admin.controller;

import com.google.common.collect.Lists;
import io.bhex.base.account.BusinessSubject;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.token.TokenCategory;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.BalanceClient;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.grpc.client.OrderClient;
import io.bhex.broker.admin.service.BalanceTransferService;
import io.bhex.broker.grpc.admin.UserAccountMap;
import io.bhex.broker.grpc.common.AccountTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:财务相关
 * @Date: 2018/9/23 下午2:37
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController extends BrokerBaseController {

    @Autowired
    private BalanceClient balanceClient;


    @RequestMapping(value = "/flows", method = RequestMethod.POST)
    public ResultModel queryFlows(@RequestBody @Valid QueryBalanceFlowsListPO po, AdminUserReply adminUser) {
        long orgId = adminUser.getOrgId();
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, orgId);
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        if (po.getCategory() == TokenCategory.FUTURE_CATEGORY_VALUE) {
            List<UserAccountMap> userAccountMaps = listUserAccount(orgId, Lists.newArrayList(combo2.getV1()), AccountTypeEnum.FUTURE);
            if (!CollectionUtils.isEmpty(userAccountMaps)) {
                combo2 = new Combo2<>(userAccountMaps.get(0).getUserId(), userAccountMaps.get(0).getAccountId());
            } else {
                return ResultModel.ok(new ArrayList<>());
            }
        }

        List<BalanceFlowRes> list = balanceClient.getBalanceFlows(orgId, combo2.getV1(), combo2.getV2(),
                        po.getBusinessSubject() != null && po.getBusinessSubject() > 0
                                ? BusinessSubject.forNumber(po.getBusinessSubject()) : null,
                        po.getTokenId(),
                        po.getNext() ? po.getFromId() : 0,
                        po.getNext() ? 0 : po.getFromId(),
                        po.getPageSize());
        return ResultModel.ok(list);
    }

//    @RequestMapping(value = "/lock", method = RequestMethod.POST)
//    public ResultModel lockBalance(@RequestBody LockBalancePO param) {
//        param.setBrokerId(getOrgId());
//        balanceTransferService.lockBalance(param);
//        return ResultModel.ok();
//    }
//
}
