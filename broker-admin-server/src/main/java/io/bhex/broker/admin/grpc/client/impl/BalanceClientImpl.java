package io.bhex.broker.admin.grpc.client.impl;

import com.google.common.collect.Lists;
import io.bhex.base.account.BalanceDetailList;
import io.bhex.base.account.BalanceServiceGrpc;
import io.bhex.base.account.BusinessSubject;
import io.bhex.base.account.GetBalanceDetailRequest;
import io.bhex.base.account.GetBatchAccountBalanceReply;
import io.bhex.base.account.GetBatchAccountBalanceRequest;
import io.bhex.base.token.QueryTokenRequest;
import io.bhex.base.token.TokenDetail;
import io.bhex.base.token.TokenServiceGrpc;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.broker.admin.controller.param.BalanceFlowRes;
import io.bhex.broker.admin.grpc.client.BalanceClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.statistics.QueryOrgBalanceFlowRequest;
import io.bhex.broker.grpc.statistics.QueryOrgBalanceFlowResponse;
import io.bhex.broker.grpc.statistics.StatisticsServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BalanceClientImpl implements BalanceClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private BalanceServiceGrpc.BalanceServiceBlockingStub getBalanceStub() {
        return grpcConfig.balanceServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
    }

//    public List<BalanceDetailDTO> getBalances(Long orgId, Long userId) {
//        BrokerUserServiceGrpc.BrokerUserServiceBlockingStub accountStub = BrokerUserServiceGrpc.newBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
//        GetAccountInfosRequest getAccountInfosRequest = GetAccountInfosRequest.newBuilder().setOrgId(orgId).setUserId(userId).build();
//        GetAccountInfosResponse response = accountStub.getAccountInfos(getAccountInfosRequest);
//        if (response.getRet() != 0) {
//            throw new BizException(ErrorCode.ERROR);
//        }
//        List<AccountInfo> infos = response.getAccountInfosList();
//        if (CollectionUtils.isEmpty(infos)) {
//            return new ArrayList<>();
//        }
//
//        //暂时只有一个userid 一个account
//        Long accountId = infos.get(0).getAccountId();
//
//        return getBalances(accountId);
//    }
//
//    public List<BalanceDetailDTO> getBalances(Long accountId) {
//        BalanceServiceGrpc.BalanceServiceBlockingStub stub = BalanceServiceGrpc.newBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
//        GetBalanceDetailRequest request = GetBalanceDetailRequest.newBuilder().setAccountId(accountId).build();
//        BalanceDetailList detailResponse = stub.getBalanceDetail(request);
//
//        log.info("{}", detailResponse.getBalanceDetailsList());
//        List<BalanceDetailDTO> list = detailResponse.getBalanceDetailsList().stream().map(detail -> {
//            BalanceDetailDTO dto = new BalanceDetailDTO();
//            BeanUtils.copyProperties(detail, dto);
//            dto.setTotal(DecimalUtil.toBigDecimal(detail.getTotal()));
//            dto.setAvailable(DecimalUtil.toBigDecimal(detail.getAvailable()));
//            dto.setLocked(DecimalUtil.toBigDecimal(detail.getLocked()));
//            dto.setTokenFullName(detail.getToken().getTokenFullName());
//            return dto;
//        }).collect(Collectors.toList());
//
//        List<TokenDetail> allTokens = queryTokens(1, 200, null);
//        for (TokenDetail detail : allTokens) {//没有持有币种显示为0
//            long count = list.stream().filter(dto -> dto.getTokenId().equals(detail.getTokenId())).count();
//            if (count > 0) {
//                continue;
//            }
//            BalanceDetailDTO dto = new BalanceDetailDTO();
//            dto.setTokenId(detail.getTokenId());
//            dto.setTotal(BigDecimal.ZERO);
//            dto.setAvailable(BigDecimal.ZERO);
//            dto.setLocked(BigDecimal.ZERO);
//            dto.setTokenFullName(detail.getTokenFullName());
//            list.add(dto);
//        }
//
//        return list;
//    }

    private List<TokenDetail> queryTokens(Integer current, Integer pageSize, String tokenId, Long orgId) {
        TokenServiceGrpc.TokenServiceBlockingStub stub = grpcConfig.tokenServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        QueryTokenRequest.Builder builder = QueryTokenRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setCurrent(current)
                .setPageSize(pageSize);
        if (tokenId != null) {
            builder.setTokenId(tokenId);
        }
        return stub.queryTokens(builder.build()).getTokenDetailsList();
    }

    @Override
    public List<BalanceFlowRes> getBalanceFlows(Long orgId, Long userId, Long accountId,
                                                BusinessSubject businessSubject, String tokenId,
                                                Long fromId, Long lastId, int limit) {
        List<Integer> subjects = new ArrayList<>();
        if (businessSubject != null) {
            subjects = Lists.newArrayList(businessSubject.getNumber());
        }

        QueryOrgBalanceFlowRequest.Builder builder = QueryOrgBalanceFlowRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setAccountId(accountId)
                .setFromId(fromId)
                //.setLastId(fromId)
                .setLimit(limit);
        if (!CollectionUtils.isEmpty(subjects)) {
            builder.addAllBusinessSubject(subjects);
        }
        if (!StringUtils.isEmpty(tokenId)) {
            builder.setTokenId(tokenId);
        }

        StatisticsServiceGrpc.StatisticsServiceBlockingStub stub = grpcConfig.statisticsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        QueryOrgBalanceFlowResponse reply = stub.queryOrgBalanceFlow(builder.build());
        List<QueryOrgBalanceFlowResponse.BalanceFlow> details = reply.getBalanceFlowList();


        List<BalanceFlowRes> resList = details.stream().map(detail -> {
            BalanceFlowRes res = new BalanceFlowRes();
            res.setUserId(Long.parseLong(detail.getUserId()));
            res.setTokenId(detail.getTokenId());
            res.setCreated(detail.getCreatedTime());
            res.setTotal(new BigDecimal(detail.getTotal()));
            res.setChanged(new BigDecimal(detail.getChanged()));
            res.setBusinessSubject(detail.getBusinessSubject());
            res.setBalanceFlowId(detail.getBalanceFlowId());
            return res;
        }).collect(Collectors.toList());
        return resList;
    }

    @Override
    public BalanceDetailList getBalanceDetail(GetBalanceDetailRequest request) {
        BalanceDetailList reply = getBalanceStub().getBalanceDetail(request);
        return reply;
    }

    @Override
    public GetBatchAccountBalanceReply getBatchAccountBalance(GetBatchAccountBalanceRequest request) {
        GetBatchAccountBalanceReply reply = getBalanceStub().getBatchAccountBalance(request);
        return reply;
    }
}


