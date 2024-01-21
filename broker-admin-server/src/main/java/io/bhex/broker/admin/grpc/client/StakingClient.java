package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.proto.AdminCommonResponse;
import io.bhex.broker.grpc.staking.*;

public interface StakingClient {

    AdminSaveProductReply saveProduct(AdminSaveProductRequest request);

    AdminGetProductDetailReply getProductDetail(AdminGetProductDetailRequest request);

    AdminGetProductListReply getProductList(AdminGetProductListRequest request);

    AdminCommonResponse onlineProduct(AdminOnlineProductRequest request);

    AdminGetBrokerProductPermissionReply getBrokerProductPermission(AdminGetBrokerProductPermissionRequest request);

    AdminQueryBrokerProductUndoRebateReply queryBrokerProductUndoRebate(AdminQueryBrokerProductUndoRebateRequest request);

    AdminQueryBrokerProductHistoryRebateReply queryBrokerProductHistoryRebate(AdminQueryBrokerProductHistoryRebateRequest request);

    /**
     * 派息转账
     *
     * @param request
     * @return
     */
    StakingProductDividendTransferResponse dividendTransfer(StakingProductDividendTransferRequest request);

    /**
     * 取消派息
     *
     * @param request
     * @return
     */
    StakingProductCancelDividendResponse cancelDividend(StakingProductCancelDividendRequest request);
}
