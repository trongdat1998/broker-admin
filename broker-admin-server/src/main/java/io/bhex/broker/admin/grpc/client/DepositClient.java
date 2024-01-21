package io.bhex.broker.admin.grpc.client;

import io.bhex.base.account.*;
import io.bhex.broker.grpc.deposit.QueryDepositOrdersRequest;
import io.bhex.broker.grpc.deposit.QueryDepositOrdersResponse;
import io.bhex.broker.grpc.statistics.QueryOrgDepositOrderRequest;
import io.bhex.broker.grpc.statistics.QueryOrgDepositOrderResponse;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 20/11/2018 5:11 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface DepositClient {

    QueryDepositOrdersResponse queryDepositOrders(QueryDepositOrdersRequest request);

    QueryOrgDepositOrderResponse queryOrgDepositOrder(QueryOrgDepositOrderRequest request);

    /**
     * 查询未入库单（独立部署--需要补全baseRequest）
     *
     * @param request
     * @return
     */
    DepositRecordList queryUnReceiptOrder(GetDepositRecordsRequest request);

    ReceiptReply receipt(ReceiptRequest request);

    AskWalletAddressReply askWalletAddress(AskWalletAddressRequest request);
}
