package io.bhex.broker.admin.grpc.client.impl;

import com.alibaba.fastjson.JSON;
import io.bhex.base.account.*;
import io.bhex.base.proto.BaseRequest;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.LockPositionDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.grpc.client.TransferClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 5:19 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class TransferClientImpl implements TransferClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Autowired
    private BrokerUserClient brokerUserClient;

    private BatchTransferServiceGrpc.BatchTransferServiceBlockingStub batchTransferServiceBlockingStub() {
        return grpcConfig.batchTransferServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME);
    }

    private BalanceServiceGrpc.BalanceServiceBlockingStub balanceServiceBlockingStub() {
        return grpcConfig.bhBalanceServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME);
    }


    @Override
    public ResultModel batchTransfer(BatchTransferPO batchTransfer, Long orgId) {
        log.info("clientOrderId {} businessType {} token {} transfers {}",
                batchTransfer.getClientOrderId(), batchTransfer.getBusinessType(), batchTransfer.getToken(), batchTransfer.getTransfers());
        if (orgId == null) {
            return ResultModel.error("Bad parameter");
        }

        if (StringUtils.isEmpty(batchTransfer.getTransfers())) {
            return ResultModel.error("Bad parameter");
        }

        List<SingleTransferPO> singleTransfers;
        try {
            singleTransfers = JSON.parseArray(batchTransfer.getTransfers(), SingleTransferPO.class);
        } catch (Exception ex) {
            log.warn("clientOrderId {} businessType {} token {} transfers {}",
                    batchTransfer.getClientOrderId(), batchTransfer.getBusinessType(), batchTransfer.getToken(), batchTransfer.getTransfers());
            return ResultModel.error("Bad parameter");
        }

        if (singleTransfers == null || singleTransfers.size() == 0) {
            return ResultModel.error("Transfers can not be empty");
        }

        if (singleTransfers.size() > 100) {
            return ResultModel.error("Not greater than 100");
        }

        //转给谁
        List<BatchTransferItem> items = buildBatchTransferItem(singleTransfers, batchTransfer, orgId);
        if (CollectionUtils.isEmpty(items)) {
            return ResultModel.error("User list is null or amount <= 0");
        }

        //谁来转
        BatchTransferRequest request = BatchTransferRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setClientTransferId(batchTransfer.getClientOrderId()) //幂等ID
                .setSourceAccountType(AccountType.OPERATION_ACCOUNT) //运营账户Type
                .setSourceOrgId(orgId)
                .setSubject(BusinessSubject.forNumber(batchTransfer.getBusinessType()))
                .addAllTransferTo(items)
                .build();
        BatchTransferResponse response = batchTransferServiceBlockingStub().batchTransfer(request);
        if (response == null || response.getErrorCode() != 0) {
            log.warn("transfer fail orgId {} clientOrderId {} batchTransfer {} msg {}",
                    orgId, batchTransfer.getClientOrderId(), JSON.toJSONString(batchTransfer), response.getMessage());
            return ResultModel.error(response.getErrorCode(), ErrorCode.TRANSFER_ERROR.getDesc());
        }

        log.info("transfer success orgId {} clientOrderId {} batchTransfer {} msg {}",
                orgId, batchTransfer.getClientOrderId(), JSON.toJSONString(batchTransfer),
                response.getMessage());
        return ResultModel.ok();
    }

    @Override
    public ResultModel transferAddLock(LockTransferPO lockTransfer, Long orgId) {
        Long accountId = brokerUserClient.getAccountId(orgId, lockTransfer.getUserId());
        if (accountId == null) {
            return ResultModel.error("user list is null");
        }

        SyncTransferRequest transferRequest = SyncTransferRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setClientTransferId(lockTransfer.getClientOrderId())
                .setSourceOrgId(orgId)
                .setSourceFlowSubject(BusinessSubject.forNumber(lockTransfer.getBusinessType()))
                .setSourceAccountType(AccountType.OPERATION_ACCOUNT)
                .setTokenId(lockTransfer.getToken())
                .setAmount(lockTransfer.getAmount())
                .setTargetAccountId(accountId)
                .setTargetOrgId(orgId)
                .setTargetAccountType(AccountType.GENERAL_ACCOUNT)
                .setToPosition(lockTransfer.getLock() == 0 ? false : true)
                .setTargetFlowSubject(BusinessSubject.forNumber(lockTransfer.getBusinessType()))
                .build();

        SyncTransferResponse response
                = batchTransferServiceBlockingStub().syncTransfer(transferRequest);
        if (response.getCodeValue() != 200) {
            log.warn("transfer fail orgId {} clientOrderId {} lockTransfer {} msg {}",
                    orgId, lockTransfer.getClientOrderId(), JSON.toJSONString(lockTransfer),
                    response.getMsg());
            return ResultModel.error(response.getCodeValue(), ErrorCode.TRANSFER_ERROR.getDesc());
        }

        log.info("transfer success orgId {} clientOrderId {} lockTransfer {} msg {}",
                orgId, lockTransfer.getClientOrderId(), JSON.toJSONString(lockTransfer),
                response.getMsg());
        return ResultModel.ok();
    }

    private List<BatchTransferItem> buildBatchTransferItem(List<SingleTransferPO> singleTransfer, BatchTransferPO batchTransfer, Long orgId) {
        List<BatchTransferItem> batchTransferItems = new ArrayList<>();

        List<SingleTransferPO> transferList
                = singleTransfer.stream().filter(s -> new BigDecimal(s.getAmount()).compareTo(BigDecimal.ZERO) <= 0).collect(Collectors.toList());


        if (transferList.size() > 0) {
            return new ArrayList<>();
        }
        singleTransfer.forEach(transfer -> {
            Long accountId = brokerUserClient.getAccountId(orgId, transfer.getUserId());
            if (accountId == null) {
                return;
            }
            BatchTransferItem item = BatchTransferItem.newBuilder()
                    .setAmount(transfer.getAmount())
                    .setTargetAccountId(accountId)
                    .setTargetAccountType(AccountType.GENERAL_ACCOUNT)
                    .setTargetOrgId(orgId)
                    .setTokenId(batchTransfer.getToken())
                    .setSubject(BusinessSubject.forNumber(batchTransfer.getBusinessType()))
                    .build();
            batchTransferItems.add(item);
        });
        return batchTransferItems;
    }

    @Override
    public ResultModel mapping(MappingPo mapping, Long orgId) {
        Long accountId = brokerUserClient.getAccountId(orgId, mapping.getTargetUserId());
        if (accountId == null) {
            return ResultModel.error("user is null");
        }

        List<SyncTransferRequest> syncTransferRequests = new ArrayList<>();
        //从运营账户转到用户 可支持释放锁仓
        syncTransferRequests.add(SyncTransferRequest.newBuilder()
                .setClientTransferId(mapping.getSourceClientOrderId())
                .setSourceOrgId(orgId)
                .setSourceFlowSubject(BusinessSubject.forNumber(mapping.getBusinessType()))
                .setSourceAccountType(AccountType.OPERATION_ACCOUNT)
                .setTokenId(mapping.getTargetToken())
                .setAmount(mapping.getTargetAmount())
                .setTargetAccountId(accountId)
                .setTargetOrgId(orgId)
                .setTargetAccountType(AccountType.GENERAL_ACCOUNT)
                .setToPosition(mapping.getIsLock() == 0 ? false : true)
                .setTargetFlowSubject(BusinessSubject.forNumber(mapping.getBusinessType()))
                .build());

        //从用户转到运营账户 不支持锁仓
        syncTransferRequests.add(SyncTransferRequest.newBuilder()
                .setClientTransferId(mapping.getTargetClientOrderId())
                .setSourceOrgId(orgId)
                .setSourceFlowSubject(BusinessSubject.forNumber(mapping.getBusinessType()))
                .setSourceAccountId(accountId)
                .setSourceAccountType(AccountType.GENERAL_ACCOUNT)
                .setTokenId(mapping.getSourceToken())
                .setAmount(mapping.getSourceAmount())
                .setTargetAccountType(AccountType.OPERATION_ACCOUNT)
                .setTargetOrgId(orgId)
                .setTargetFlowSubject(BusinessSubject.forNumber(mapping.getBusinessType()))
                .build());

        if (syncTransferRequests.size() == 0) {
            return ResultModel.error("Sync array is null");
        }

        BatchSyncTransferRequest batchSyncTransferRequest = BatchSyncTransferRequest
                .newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .addAllSyncTransferRequestList(syncTransferRequests)
                .build();

        SyncTransferResponse response = batchTransferServiceBlockingStub().batchSyncTransfer(batchSyncTransferRequest);
        if (response == null || response.getCodeValue() != 200) {
            log.info("transfer success orgId {} sourceClientOrderId {}  targetClientOrderId {} batchTransfer {} msg {}",
                    orgId, mapping.getSourceClientOrderId(), mapping.getTargetClientOrderId(), JSON.toJSONString(mapping),
                    response.getMsg());
            return ResultModel.error(response.getCodeValue(), ErrorCode.TRANSFER_ERROR.getDesc());
        }

        log.info("transfer success orgId {} sourceClientOrderId {}  targetClientOrderId {} batchTransfer {} msg {}",
                orgId, mapping.getSourceClientOrderId(), mapping.getTargetClientOrderId(), JSON.toJSONString(mapping),
                response.getMsg());
        return ResultModel.ok();
    }

    @Override
    public ResultModel getLockPosition(GetPositionPo getPosition, Long orgId) {
        Long accountId = brokerUserClient.getAccountId(orgId, getPosition.getUserId());
        if (accountId == null) {
            return ResultModel.error("Not find account");
        }

        GetPositionRequest getPositionRequest = GetPositionRequest
                .newBuilder()
                .setAccountId(accountId)
                .setBrokerId(orgId)
                .setTokenId(getPosition.getTokenId())
                .build();

        PositionResponseList positionResponseList
                = balanceServiceBlockingStub().getPosition(getPositionRequest);


        if (positionResponseList == null || positionResponseList.getCode() != 1) {
            return ResultModel.error("Not find position info");
        }

        if (positionResponseList.getPositionListList().size() == 0) {
            return ResultModel.error("Not find position info");
        }

        List<LockPositionDTO> lockPositions
                = buildLockPositionInfo(positionResponseList.getPositionListList());

        if (lockPositions.size() > 0) {
            return ResultModel.ok(lockPositions);
        }
        return ResultModel.ok(new ArrayList<>());
    }


    @Override
    public ResultModel unlockBalance(UnlockBalancePO unlockBalance, Long orgId) {
        Long accountId = brokerUserClient.getAccountId(orgId, unlockBalance.getUserId());
        if (accountId == null) {
            return ResultModel.error("Not find account");
        }

        //从锁仓解锁到balance可用
        UnlockBalanceRequest unlockBalanceRequest = UnlockBalanceRequest
                .newBuilder()
                .setAccountId(accountId)
                .setClientReqId(unlockBalance.getClientOrderId())
                .setTokenId(unlockBalance.getToken())
                .setBaseRequest(BaseRequest.newBuilder().setOrganizationId(orgId).setBrokerUserId(unlockBalance.getUserId()).build())
                .setUnlockAmount(unlockBalance.getUnlockAmount())
                .setUnlockReason(unlockBalance.getUnlockReason())
                .setUnlockFromPositionLocked(false)
                .build();

        UnlockBalanceResponse unlockBalanceResponse
                = balanceServiceBlockingStub().unlockBalance(unlockBalanceRequest);
        if (unlockBalanceResponse.getCodeValue() != 200) {
            log.info("unlock error modelInfo {} msg : {} ", JSON.toJSONString(unlockBalance), unlockBalanceResponse.getMsg());
            return ResultModel.error(unlockBalanceResponse.getCodeValue(), unlockBalanceResponse.getMsg());
        }
        log.info("unlock error modelInfo {} msg : {} ", JSON.toJSONString(unlockBalance), unlockBalanceResponse.getMsg());
        return ResultModel.ok();
    }

    /**
     * @param positions
     * @return
     */
    private List<LockPositionDTO> buildLockPositionInfo(List<Position> positions) {
        List<LockPositionDTO> lockPositions = new ArrayList<>();
        positions.stream().forEach(s -> {
            LockPositionDTO lockPositionDTO = LockPositionDTO
                    .builder()
                    .accountId(s.getAccountId())
                    .available(s.getAvailable())
                    .locked(s.getLocked())
                    .orgId(s.getBrokerId())
                    .tokenId(s.getTokenId())
                    .total(s.getTotal())
                    .build();
            lockPositions.add(lockPositionDTO);
        });
        return lockPositions;
    }


    public static void main(String[] args) {
        LockPositionDTO lockPositionDTO = new LockPositionDTO();
        lockPositionDTO.setAccountId(11L);
        lockPositionDTO.setAvailable("100.00");
        lockPositionDTO.setLocked("40.00");
        lockPositionDTO.setOrgId(6001);
        lockPositionDTO.setTokenId("BTC");
        lockPositionDTO.setTotal("140.00");
        List<LockPositionDTO> lockPositionDTOS = new ArrayList<>();
        lockPositionDTOS.add(lockPositionDTO);
        System.out.println(JSON.toJSONString(lockPositionDTOS));

        String string = "[{\"userId\":236747246331256832,\"amount\":\"100.10\"},{\"userId\":236747246331256833,\"amount\":\"99.10\"}]";
        List<SingleTransferPO> transferPOS = JSON.parseArray(string, SingleTransferPO.class);

        System.out.println(transferPOS.size());
    }
}
