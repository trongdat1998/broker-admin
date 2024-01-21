package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.param.LockBalancePO;
import io.bhex.broker.admin.controller.param.UnlockBalancePO;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 2019/5/26 5:49 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
public interface BalanceTransferService {

    Boolean lockBalance(LockBalancePO param);

    Boolean unlockBalance(UnlockBalancePO param);

}
