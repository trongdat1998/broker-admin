package io.bhex.broker.admin.grpc.client;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.param.*;


/**
 * 转账client
 */
public interface TransferClient {

    /**
     * 批量转账
     *
     * @param batchTransfer batchTransfer
     * @param orgId         orgId
     * @return resultModel
     */
    ResultModel batchTransfer(BatchTransferPO batchTransfer, Long orgId);

    /**
     * 转账+锁仓
     *
     * @param lockTransfer lockTransfer
     * @param orgId        orgId
     * @return resultModel
     */
    ResultModel transferAddLock(LockTransferPO lockTransfer, Long orgId);

    /**
     * 映射闪兑功能
     *
     * @param mapping mapping
     * @param orgId   orgId
     * @return resultModel
     */
    ResultModel mapping(MappingPo mapping, Long orgId);

    /**
     * 获取用户锁仓信息
     *
     * @param getPosition getPosition
     * @param orgId       orgId
     * @return resultModel
     */
    ResultModel getLockPosition(GetPositionPo getPosition, Long orgId);

    /**
     * 解锁仓位
     *
     * @param unlockBalance
     * @param orgId
     * @return
     */
    ResultModel unlockBalance(UnlockBalancePO unlockBalance, Long orgId);
}
