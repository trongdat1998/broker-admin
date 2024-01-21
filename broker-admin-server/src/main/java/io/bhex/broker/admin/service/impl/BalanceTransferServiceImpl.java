package io.bhex.broker.admin.service.impl;

import io.bhex.base.account.*;
import io.bhex.base.proto.BaseRequest;
import io.bhex.broker.admin.controller.param.LockBalancePO;
import io.bhex.broker.admin.controller.param.UnlockBalancePO;
import io.bhex.broker.admin.grpc.client.BalanceTransferClient;
import io.bhex.broker.admin.service.BalanceTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 2019/5/26 5:50 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Service
public class BalanceTransferServiceImpl implements BalanceTransferService {

    @Autowired
    private BalanceTransferClient balanceTransferClient;

    @Override
    public Boolean lockBalance(LockBalancePO param) {
        SyncTransferRequest request = SyncTransferRequest.newBuilder()
                .setBaseRequest(BaseRequest.newBuilder().setOrganizationId(param.getBrokerId()).build())
                .setTargetAccountId(param.getAccountId())
                .setSourceFlowSubject(BusinessSubject.ICO_UNLOCK)
                .build();

        balanceTransferClient.syncTransfer(request);
        return null;
    }

    @Override
    public Boolean unlockBalance(UnlockBalancePO param) {
        return null;
    }
}
