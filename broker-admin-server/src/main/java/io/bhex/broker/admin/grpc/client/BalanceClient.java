package io.bhex.broker.admin.grpc.client;

import io.bhex.base.account.*;
import io.bhex.bhop.common.dto.param.BalanceDetailDTO;
import io.bhex.broker.admin.controller.param.BalanceFlowRes;

import java.util.List;

public interface BalanceClient  extends BaseClient{

    //List<BalanceDetailDTO> getBalances(Long orgId, Long userId);

    List<BalanceFlowRes> getBalanceFlows(Long orgId, Long userId, Long accountId,
                                         BusinessSubject businessSubject, String tokenId,
                                         Long fromId, Long lastId, int limit);

    //List<BalanceDetailDTO> getBalances( Long accountId);

    /**
     * 查询账户余额接口(独立部署--补全baseRequest)
     *
     * @param request
     * @return
     */
    BalanceDetailList getBalanceDetail(GetBalanceDetailRequest request);

    /**
     * 批量查询账户余额接口(独立部署--补全baseRequest)
     * @param request
     * @return
     */
    GetBatchAccountBalanceReply getBatchAccountBalance(GetBatchAccountBalanceRequest request);
}
