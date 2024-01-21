package io.bhex.broker.admin.service;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.constant.AdminTokenTypeEnum;
import io.bhex.base.token.TokenDetail;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.broker.admin.controller.dto.SimpleTokenDTO;
import io.bhex.broker.admin.controller.dto.TokenApplyRecordDTO;
import io.bhex.broker.admin.controller.dto.TokenDTO;
import io.bhex.broker.admin.controller.param.BrokerTokenTaskConfigDTO;
import io.bhex.broker.admin.controller.param.ExtraRequestPO;
import io.bhex.broker.admin.controller.param.TokenApplyPO;
import io.bhex.broker.grpc.common.AdminSimplyReply;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 11:55 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface TokenService {

    List<String> queryQuoteTokens(Long brokerId);

    PaginationVO<TokenDTO> queryToken(Integer current, Integer pageSize, Integer category, String tokenName, Long brokerId, List<ExtraRequestPO> extraRequestInfos);

    PaginationVO<TokenDTO> queryTokenById(Integer current, Integer pageSize, Integer category, String tokenId, Long brokerId, List<ExtraRequestPO> extraRequestInfos);

    List<SimpleTokenDTO> querySimpleTokens(Long brokerId, Integer category);

    List<TokenDTO> queryTokenByBrokerId(Integer current, Integer pageSize, Long brokerId);

    Boolean allowDeposit(String tokenId, Boolean allowDeposit, Long brokerId);

    Boolean allowWithdraw(String tokenId, Boolean allowWithdraw, Long brokerId);

    Boolean publish(String tokenId, Boolean isPublish, Long brokerId);

    void syncBhexTokens(Long brokerId);

    List<TokenDTO> listTokenByOrgId(Long orgId, Integer category);

    TokenDetail getTokenFromBh(String tokenId, Long orgId);

    boolean setHighRiskToken(Long orgId, String tokenId, Boolean isHighRiskToken);

    boolean setWithdrawFee(Long orgId, String tokenId, BigDecimal withdrawFee);

    TokenApplyRecordDTO getApplyRecordById(long brokerId, Long applyRecordId);

    TokenApplyRecordDTO getApplyRecordByTokenId(long brokerId, String tokenId);

    int applyToken(Long exchangeId, TokenApplyPO tokenRecordPO, AdminTokenTypeEnum tokenTypeEnum);

    PaginationVO<TokenApplyRecordDTO> listTokenApplyRecords(Long brokerId, Integer tokenType, Integer current, Integer pageSize);

    AdminSimplyReply editTokenName(long orgId, String tokenId, String newTokenName);

    AdminSimplyReply editTokenFullName(long orgId, String tokenId, String newTokenFullName);

    AdminSimplyReply editTokenExtraTags(long orgId, String tokenId, Map<String, Integer> tagMap);

    AdminSimplyReply editTokenExtraConfigs(long orgId, String tokenId, Map<String, String> configMap);

    void editTokenTaskConfig(Long orgId, AdminUserReply adminUserReply, BrokerTokenTaskConfigDTO po);

    List<BrokerTokenTaskConfigDTO> getTokenTaskConfigs(Long orgId, Integer pageSize, Long fromId);

}
