package io.bhex.broker.admin.grpc.client;

import io.bhex.base.bhadmin.QueryApplyTokenRecordRequest;
import io.bhex.base.bhadmin.TokenApplyObj;
import io.bhex.base.bhadmin.TokenApplyRecordList;
import io.bhex.base.token.GetTokenRequest;
import io.bhex.base.token.QueryTokensReply;
import io.bhex.base.token.TokenDetail;
import io.bhex.broker.grpc.admin.QueryTokenReply;
import io.bhex.broker.grpc.admin.QueryTokenSimpleReply;
import io.bhex.broker.grpc.admin.QueryTokenSimpleRequest;
import io.bhex.broker.grpc.basic.QueryQuoteTokenRequest;
import io.bhex.broker.grpc.basic.QueryQuoteTokenResponse;
import io.bhex.broker.grpc.common.AdminSimplyReply;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 5:17 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface TokenClient {

    QueryTokenSimpleReply queryTokenSimple(QueryTokenSimpleRequest request);

    QueryTokenReply queryToken(Integer current, Integer pageSize, Integer category, String tokenId, String tokenName, Long brokerId);

    QueryTokensReply queryBrokerTokens(Integer current, Integer pageSize, String tokenId, Long brokerId);

    Boolean allowDeposit(String tokenId, Boolean allowDeposit, Long brokerId);

    Boolean allowWithdraw(String tokenId, Boolean allowWithdraw, Long brokerId);

    Boolean publish(String tokenId, Boolean isPublish, Long brokerId);

    void syncBhexTokens(Long brokerId);

    TokenDetail getToken(GetTokenRequest request);

    List<TokenDetail> getTokenListByIds(long brokerId, List<String> tokenIds);

    QueryQuoteTokenResponse queryQuoteTokens(QueryQuoteTokenRequest request);

    QueryTokenReply listTokenByOrgId(Long brokerId,Integer category);

    boolean setHighRiskToken(Long orgId, String tokenId, Boolean isHighRiskToken);

    boolean setWithdrawFee(Long orgId, String tokenId, BigDecimal withdrawFee);

    TokenApplyObj queryApplyTokenRecord(QueryApplyTokenRecordRequest request);

    int applyToken(TokenApplyObj tokenRecord);

    TokenApplyRecordList listTokenApplyRecords(Long brokerId, Integer tokenType, Integer current, Integer pageSize);

    AdminSimplyReply editTokenName(long orgId, String tokenId, String newTokenName);

    AdminSimplyReply editTokenFullName(long orgId, String tokenId, String newTokenFullName);

    AdminSimplyReply editTokenExtraTags(long orgId, String tokenId, Map<String, Integer> tagMap);

    AdminSimplyReply editTokenExtraConfigs(long orgId, String tokenId, Map<String, String> configMap);

}
