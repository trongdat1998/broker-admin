package io.bhex.broker.admin.grpc.client;

import io.bhex.base.account.*;
import io.bhex.broker.grpc.admin.QueryUnverfiedOrdersResponse;
import io.bhex.broker.grpc.admin.QueryVerfiedOrdersResponse;
import io.bhex.broker.grpc.admin.QueryWithdrawOrderResponse;
import io.bhex.broker.grpc.admin.VerifyOrderResponse;
import io.bhex.broker.grpc.statistics.QueryOrgWithdrawOrderRequest;
import io.bhex.broker.grpc.statistics.QueryOrgWithdrawOrderResponse;

public interface WithdrawOrderClient {


    //由于平台和broker数据状态不一致 改用平台数据
    @Deprecated
    QueryUnverfiedOrdersResponse queryUnverfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize);

    GetBrokerAuditingResponse queryUnverfiedOrdersFromBh(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize);

    GetWithdrawalOrderResponse getWithdrawalOrderFromBh(GetWithdrawalOrderRequest request);

    //public List<WithdrawOrderUnverifyListRes> queryUnverfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize);

    VerifyOrderResponse verify(Long brokerId, Long userId, Long withdrawOrderId, boolean verifyPassed, String remark,
                               String adminUserName, Integer failedReason, String refuseReason);
    //ResultModel verify(Long brokerId, Long withdrawOrderId, boolean verifyPassed, String remark, String adminUserName);

    QueryVerfiedOrdersResponse queryVerfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize);

    QueryWithdrawOrderResponse queryWithdrawOrder(Long brokerId, Long withdrawOrderId);

    /**
     * 调用bh平台服务完成提现的通过或者拒绝
     *
     * @param brokerId
     * @param bhWithdrawOrderId
     * @param passed            true-通过  false-拒绝
     * @return
     */
    SetWithdrawalAuditStatusResponse setWithdrawalAuditStatus(Long brokerId, Long accountId, Long bhWithdrawOrderId, boolean passed, Integer failedReason);

    /**
     * 查询提现记录
     *
     * @param request
     * @return
     */
    //QueryWithdrawOrdersResponse queryWithdrawOrders(QueryWithdrawOrdersRequest request);

    QueryOrgWithdrawOrderResponse queryOrgWithdrawOrder(QueryOrgWithdrawOrderRequest request);

    QueryOrdersReply queryWithdrawOrderFromBh(io.bhex.base.account.QueryOrdersRequest request);
}
