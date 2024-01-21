package io.bhex.broker.admin.service;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.DepositDTO;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 20/11/2018 5:54 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface DepositService {

    List<DepositDTO> queryDepositOrders(Long brokerId, Long userId, Long fromId, Long endId, Integer pageSize,
                                        String tokenId, Long startTime, Long endTime, String address, String txId);

    List<DepositDTO> queryUnReceipts(Long accountId, Long userId, Long fromId, Long endId,
                                     Integer pageSize, String tokenId, Long startTime, Long endTime, String txId, Long orgId);

    ResultModel execReceipt(Long orgId, Long accountId, Long orderId);

}
