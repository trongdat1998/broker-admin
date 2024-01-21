package io.bhex.broker.admin.grpc.client.impl;

import com.google.common.base.Strings;
import io.bhex.base.bhadmin.*;
import io.bhex.base.token.GetTokenIdsRequest;
import io.bhex.base.token.GetTokenRequest;
import io.bhex.base.token.QueryTokensReply;
import io.bhex.base.token.SaasTokenServiceGrpc;
import io.bhex.base.token.TokenDetail;
import io.bhex.base.token.QueryBrokerTokensRequest;
import io.bhex.base.token.TokenServiceGrpc;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.broker.admin.grpc.client.TokenClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.QueryTokenRequest;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.basic.BasicServiceGrpc;
import io.bhex.broker.grpc.basic.QueryQuoteTokenRequest;
import io.bhex.broker.grpc.basic.QueryQuoteTokenResponse;
import io.bhex.broker.grpc.common.AdminSimplyReply;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 5:18 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class TokenClientImpl implements TokenClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminTokenServiceGrpc.AdminTokenServiceBlockingStub getTokenStub() {
        return grpcConfig.adminTokenServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private SaasTokenServiceGrpc.SaasTokenServiceBlockingStub getBhexTokenStub() {
        return grpcConfig.saasTokenServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
    }

    public TokenServiceGrpc.TokenServiceBlockingStub getTokenDetailStub() {
        return grpcConfig.tokenServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME);
    }

    @Override
    public QueryTokenSimpleReply queryTokenSimple(QueryTokenSimpleRequest request) {
        return getTokenStub().queryTokenSimple(request);
    }

    @Override
    public QueryTokenReply queryToken(Integer current, Integer pageSize, Integer category, String tokenId, String tokenName, Long brokerId) {
        if (category == null) {
            category = 0;
        }

        QueryTokenRequest request = QueryTokenRequest.newBuilder()
                .setCurrent(current)
                .setPageSize(pageSize)
                .setBrokerId(brokerId)
                .setCategory(category)
                .setTokenId(Strings.nullToEmpty(tokenId).toUpperCase())
                .setTokenName(Strings.nullToEmpty(tokenName).toUpperCase())
                .build();

        return getTokenStub().queryToken(request);
    }

    @Override
    public QueryTokensReply queryBrokerTokens(Integer current, Integer pageSize, String tokenId, Long brokerId) {
        QueryBrokerTokensRequest request = QueryBrokerTokensRequest.newBuilder()
                .setCurrent(current)
                .setPageSize(pageSize)
                .setBrokerId(brokerId)
                .setTokenId(tokenId)
                .build();
        QueryTokensReply reply = getBhexTokenStub().queryBrokerTokens(request);
        return reply;
    }

    @Override
    public Boolean allowDeposit(String tokenId, Boolean allowDeposit, Long brokerId) {
        TokenAllowDepositRequest request = TokenAllowDepositRequest.newBuilder()
                .setTokenId(tokenId)
                .setAllowTrade(allowDeposit)
                .setBrokerId(brokerId)
                .build();

        return getTokenStub().tokenAllowDeposit(request).getResult();
    }

    @Override
    public Boolean allowWithdraw(String tokenId, Boolean allowWithdraw, Long brokerId) {
        TokenAllowWithdrawRequest request = TokenAllowWithdrawRequest.newBuilder()
                .setTokenId(tokenId)
                .setAllowWithd(allowWithdraw)
                .setBrokerId(brokerId)
                .build();

        return getTokenStub().tokenAllowWithdraw(request).getResult();
    }

    @Override
    public Boolean publish(String tokenId, Boolean isPublish, Long brokerId) {
        TokenPublishRequest request = TokenPublishRequest.newBuilder()
                .setTokenId(tokenId)
                .setPublished(isPublish)
                .setBrokerId(brokerId)
                .build();

        return getTokenStub().tokenPublish(request).getResult();
    }

    @Override
    public TokenDetail getToken(GetTokenRequest request) {
        return getTokenDetailStub().getToken(request);
    }

    @Override
    public List<TokenDetail> getTokenListByIds(long brokerId, List<String> tokenIds) {
        GetTokenIdsRequest request = GetTokenIdsRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(brokerId))
                .addAllTokenIds(tokenIds)
                .build();
        return getTokenDetailStub().getTokenListByIds(request).getTokenDetailsList();
    }

    @Override
    public QueryQuoteTokenResponse queryQuoteTokens(QueryQuoteTokenRequest request) {
        BasicServiceGrpc.BasicServiceBlockingStub stub = grpcConfig.basicServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        QueryQuoteTokenResponse response = stub.queryQuoteTokens(request);
        return response;
    }

    @Override
    public QueryTokenReply listTokenByOrgId(Long brokerId, Integer category) {

        QueryTokenRequest request = QueryTokenRequest.newBuilder()
                .setBrokerId(brokerId)
                .setCategory(category)
                .build();

        return getTokenStub().listTokenByOrgId(request);
    }

    @Async
    @Override
    public void syncBhexTokens(Long brokerId) {
        Integer current = 1;
        Integer pageSize = 20;
        Boolean isOver = false;

        while (!isOver) {
            QueryBrokerTokensRequest request = QueryBrokerTokensRequest.newBuilder()
                    .setCurrent(current)
                    .setPageSize(pageSize)
                    .setBrokerId(brokerId)
                    .build();
            QueryTokensReply reply = getBhexTokenStub().queryBrokerTokens(request);
            List<io.bhex.base.token.TokenDetail> tokenDetailsList = reply.getTokenDetailsList();

            if (!CollectionUtils.isEmpty(tokenDetailsList)) {
                for (io.bhex.base.token.TokenDetail d : tokenDetailsList) {
                    AddTokenRequest token = AddTokenRequest.newBuilder()
                            .setTokenId(d.getTokenId())
                            .setTokenName(d.getTokenName())
                            .setTokenFullName(d.getTokenFullName())
                            .setMinPrecision(d.getMinPrecision())
                            .setAddressType(d.getAddressType())
                            .setDepositMinQuantity(d.getDepositMinQuantity().getStr())
                            .setAllowDeposit(d.getAllowDeposit())
                            .setAllowWithdraw(d.getAllowWithdraw())
                            .setIcon(d.getIcon())
                            .setTokenDetail(d.getTokenDetail())
                            .setBrokerId(brokerId)
                            .setCategory(d.getCategoryValue())
                            .build();
                    getTokenStub().addToken(token);
                }
                isOver = ((++current - 1) * pageSize) > reply.getTotal();
            } else {
                isOver = true;
            }

        }

    }

    @Override
    public boolean setHighRiskToken(Long orgId, String tokenId, Boolean isHighRiskToken) {
        SetTokenHighRiskRequest request = SetTokenHighRiskRequest.newBuilder()
                .setOrgId(orgId)
                .setTokenId(tokenId)
                .setIsHighRiskToken(isHighRiskToken)
                .build();
        return getTokenStub().setTokenHighRisk(request).getResult();
    }

    @Override
    public boolean setWithdrawFee(Long orgId, String tokenId, BigDecimal withdrawFee) {
        SetTokenWithdrawFeeRequest request = SetTokenWithdrawFeeRequest.newBuilder()
                .setOrgId(orgId)
                .setTokenId(tokenId)
                .setWithdrawFee(withdrawFee.stripTrailingZeros().toPlainString())
                .build();
        return getTokenStub().setTokenWithdrawFee(request).getResult();
    }

    @Override
    public TokenApplyObj queryApplyTokenRecord(QueryApplyTokenRecordRequest request) {
        AdminTokenApplyServiceGrpc.AdminTokenApplyServiceBlockingStub stub =
                grpcConfig.adminTokenApplyServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        return stub.queryApplyTokenRecord(request);
    }

    @Override
    public int applyToken(TokenApplyObj tokenRecord) {
        AdminTokenApplyServiceGrpc.AdminTokenApplyServiceBlockingStub stub =
                grpcConfig.adminTokenApplyServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        TokenApplyObj curTokenRecord = stub.queryApplyTokenRecord(QueryApplyTokenRecordRequest.newBuilder()
                .setBrokerId(tokenRecord.getBrokerId()).setTokenId(tokenRecord.getTokenId()).build());
        if (curTokenRecord.getId() != 0) {
            tokenRecord = tokenRecord.toBuilder()
                    .setMinPrecision(curTokenRecord.getMinPrecision())
                    .setNeedTag(curTokenRecord.getNeedTag())
                    .setPlatformFee(curTokenRecord.getPlatformFee())
                    .setConfirmCount(curTokenRecord.getConfirmCount())
                    .setExploreUrl(curTokenRecord.getExploreUrl())
                    .build();
        }
        ApplyTokenResult result = stub.applyToken(tokenRecord);
        int res = result.getRes();
        if (res == -1) {
            throw new BizException(ErrorCode.TOKEN_ALREADY_EXIST);
        }
        if (res == -2) {
            throw new BizException(ErrorCode.FORBIDDEN_EDIT);
        }
        return res;
    }

    @Override
    public TokenApplyRecordList listTokenApplyRecords(Long brokerId, Integer tokenType, Integer current, Integer pageSize) {
        AdminTokenApplyServiceGrpc.AdminTokenApplyServiceBlockingStub stub =
                grpcConfig.adminTokenApplyServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        return stub.listTokenApplyRecords(GetTokenPager.newBuilder()
                .setBrokerId(brokerId)
                .setTokenType(tokenType)
                .setState(-1)
                .setStart(current)
                .setSize(pageSize)
                .build());
    }

    @Override
    public AdminSimplyReply editTokenName(long orgId, String tokenId, String newTokenName) {
        EditTokenNameRequest request = EditTokenNameRequest.newBuilder()
                .setOrgId(orgId)
                .setTokenId(tokenId)
                .setTokenName(newTokenName)
                .build();
        return getTokenStub().editTokenName(request);
    }

    @Override
    public AdminSimplyReply editTokenFullName(long orgId, String tokenId, String newTokenFullName) {
        EditTokenFullNameRequest request = EditTokenFullNameRequest.newBuilder()
                .setOrgId(orgId)
                .setTokenId(tokenId)
                .setTokenFullName(newTokenFullName)
                .build();
        return getTokenStub().editTokenFullName(request);
    }

    @Override
    public AdminSimplyReply editTokenExtraTags(long orgId, String tokenId, Map<String, Integer> tagMap) {
        EditTokenExtraTagsRequest request = EditTokenExtraTagsRequest.newBuilder()
                .setOrgId(orgId)
                .setTokenId(tokenId)
                .putAllExtraTag(tagMap)
                .build();
        return getTokenStub().editTokenExtraTags(request);
    }

    @Override
    public AdminSimplyReply editTokenExtraConfigs(long orgId, String tokenId, Map<String, String> configMap) {
        EditTokenExtraConfigsRequest request = EditTokenExtraConfigsRequest.newBuilder()
                .setOrgId(orgId)
                .setTokenId(tokenId)
                .putAllExtraConfig(configMap)
                .build();
        return getTokenStub().editTokenExtraConfigs(request);
    }
}
