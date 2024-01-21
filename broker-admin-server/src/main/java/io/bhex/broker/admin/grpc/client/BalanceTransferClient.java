package io.bhex.broker.admin.grpc.client;

import io.bhex.base.account.*;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 08/11/2018 4:25 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BalanceTransferClient {

    /**
     * 批量转账接口（独立部署--补全baseRequest）
     *
     * @param request
     * @return
     */
    BatchTransferResponse batchTransfer(BatchTransferRequest request);

    /**
     * // 实时转账。一对一（独立部署--补全baseRequest）
     *
     * @param request
     * @return
     */
    SyncTransferResponse syncTransfer(SyncTransferRequest request);
}
