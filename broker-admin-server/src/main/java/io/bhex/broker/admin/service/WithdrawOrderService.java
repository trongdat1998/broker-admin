package io.bhex.broker.admin.service;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.AskWalletAddressDTO;
import io.bhex.broker.admin.controller.dto.WithdrawOrderDTO;
import io.bhex.broker.admin.controller.param.WithdrawOrderUnverifyListRes;
import io.bhex.broker.admin.controller.param.WithdrawOrderVerifyListRes;

import java.util.List;


/**
 * @Description:
 * @Date: 2018/9/20 下午4:22
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface WithdrawOrderService {

    List<WithdrawOrderUnverifyListRes> queryUnverfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize);

    ResultModel verify(Long brokerId, Long userId, Long accountId, Long withdrawOrderId, boolean verifyPassed, String remark,
                       String adminUserName, Integer failedReason, String refuseReason);

    List<WithdrawOrderVerifyListRes> queryVerfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize);

    List<WithdrawOrderDTO> queryWithdrawOrder(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize,
                                              String tokenId, Long startTime, Long endTime, String address, String txId);

    AskWalletAddressDTO askWalletAddress(String tokenId, String address, String memo, Long orgId);

}
