package io.bhex.broker.admin.service.impl;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.bhex.base.account.ExchangeReply;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.bhadmin.QueryApplyTokenRecordRequest;
import io.bhex.base.bhadmin.TokenApplyObj;
import io.bhex.base.bhadmin.TokenApplyRecordList;
import io.bhex.bhop.common.constant.AdminTokenTypeEnum;
import io.bhex.base.token.GetTokenRequest;
import io.bhex.base.token.QueryTokensReply;
import io.bhex.base.token.TokenCategory;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.broker.admin.controller.dto.BaseConfigDTO;
import io.bhex.broker.admin.controller.dto.SimpleTokenDTO;
import io.bhex.broker.admin.controller.dto.TokenApplyRecordDTO;
import io.bhex.broker.admin.controller.dto.TokenDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.BrokerTokenTaskConfigDTO;
import io.bhex.broker.admin.controller.param.ExtraRequestPO;
import io.bhex.broker.admin.controller.param.TokenApplyPO;
import io.bhex.broker.admin.grpc.client.TokenClient;
import io.bhex.broker.admin.grpc.client.impl.OrgClient;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.service.TokenService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.common.util.ExtraConfigUtil;
import io.bhex.broker.common.util.ExtraTagUtil;
import io.bhex.broker.grpc.admin.QueryTokenReply;
import io.bhex.broker.grpc.admin.QueryTokenSimpleRequest;
import io.bhex.broker.grpc.admin.SimpleToken;
import io.bhex.broker.grpc.admin.TokenDetail;
import io.bhex.broker.grpc.basic.QueryQuoteTokenRequest;
import io.bhex.broker.grpc.basic.QueryQuoteTokenResponse;
import io.bhex.broker.grpc.basic.QuoteToken;
import io.bhex.broker.grpc.common.AdminSimplyReply;
import io.bhex.broker.grpc.common.Header;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 3:47 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    BrokerTaskConfigService brokerTaskConfigService;
    @Autowired
    private TokenClient tokenClient;
    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;
    @Autowired
    private OrgClient orgClient;


//    private Cache<Long, List<String>> localCache = CacheBuilder
//            .newBuilder()
//            .expireAfterWrite(1800L, TimeUnit.SECONDS)
//            .build();

    @Override
    public List<String> queryQuoteTokens(Long brokerId) {
        //List<String> tokens = localCache.getIfPresent(brokerId);
//        if (!CollectionUtils.isEmpty(tokens)) {
//            log.info("queryQuoteTokens from cache:{}", brokerId);
//            return tokens;
//        }
        List<Integer> coinCategories = Arrays.asList(
                TokenCategory.MAIN_CATEGORY.getNumber(),
                TokenCategory.INNOVATION_CATEGORY.getNumber()
        );
        Header header = Header.newBuilder().setOrgId(brokerId).build();
        QueryQuoteTokenResponse response = tokenClient
                .queryQuoteTokens(QueryQuoteTokenRequest.newBuilder().setHeader(header).addAllCategory(coinCategories).build());
        List<QuoteToken> list = response.getQuoteTokensList();
        list = list.stream().sorted(Comparator.comparing(QuoteToken::getCustomOrder)).collect(Collectors.toList());
        //Map<Long, List<QuoteToken>> group = list.stream().collect(Collectors.groupingBy(QuoteToken::getOrgId));

        return list.stream().map(q -> q.getTokenId()).collect(Collectors.toList());
    }

    @Override
    public List<SimpleTokenDTO> querySimpleTokens(Long brokerId, Integer category) {
        QueryTokenSimpleRequest request = QueryTokenSimpleRequest.newBuilder()
                .setBrokerId(brokerId).setCategory(category)
                .build();
        List<SimpleToken> tokens = tokenClient.queryTokenSimple(request).getTokenDetailsList();
        return tokens.stream().map(t -> {
            SimpleTokenDTO dto = new SimpleTokenDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(t, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public PaginationVO<TokenDTO> queryTokenById(Integer current, Integer pageSize, Integer category, String tokenId, Long brokerId, List<ExtraRequestPO> extraRequestInfos) {
        QueryTokenReply reply = tokenClient.queryToken(current, pageSize, category, tokenId, "", brokerId);
        return queryToken(brokerId, reply);
    }

    public PaginationVO<TokenDTO> queryToken(long brokerId, QueryTokenReply reply) {
        PaginationVO<TokenDTO> vo = new PaginationVO();
        BeanUtils.copyProperties(reply, vo);

        List<TokenDTO> dtos = new ArrayList<>();
        List<TokenDetail> tokens = reply.getTokenDetailsList();

        Map<String, io.bhex.base.token.TokenDetail> baasMap = new HashMap<>();
        List<String> tokenIds = tokens.stream().map(o -> o.getTokenId()).distinct().collect(Collectors.toList());
        List<io.bhex.base.token.TokenDetail> tokenDetails = tokenClient.getTokenListByIds(brokerId, tokenIds);
        tokenDetails.forEach(t -> baasMap.put(t.getTokenId(), t));

        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup("saas.broker.switch");
        configPO.setKey("aggregate_allow_deposit_withdraw");
        configPO.setWithLanguage(false);


        BaseConfigDTO configDTO = baseConfigService.getOneConfig(brokerId, configPO);
        List<String> allowDepositWithdrawList = configDTO != null ? Lists.newArrayList(configDTO.getValue().split(",")) : Lists.newArrayList();

        tokens = tokens.stream().filter(t -> {
            String parentTokenId = baasMap.getOrDefault(t.getTokenId(), io.bhex.base.token.TokenDetail.getDefaultInstance()).getParentTokenId();
            return Strings.isNullOrEmpty(parentTokenId);
        }).collect(Collectors.toList());
        for (TokenDetail detail : tokens) {
            TokenDTO dto = new TokenDTO();
            BeanUtils.copyProperties(detail, dto);

            dto.setIsPublished(detail.getStatus() == 1);
            dto.setWithdrawToken(detail.getFeeTokenId());
            dto.setIsHighRiskToken(detail.getIsHighRiskToken());
            dto.setFee(detail.getWithdrawFee());
            dto.setTags(ExtraTagUtil.newInstance(detail.getExtraTagMap()).map());
            dto.setConfigs(ExtraConfigUtil.newInstance(detail.getExtraConfigMap()).map());
            dto.setDepositMinQuantity(detail.getDepositMinQuantity());

            io.bhex.base.token.TokenDetail bhToken = baasMap.getOrDefault(detail.getTokenId(), io.bhex.base.token.TokenDetail.getDefaultInstance());
            dto.setIsBaas(bhToken.getIsBaas());
            dto.setIsAggregate(bhToken.getIsAggregate());
            dto.setIsTest(bhToken.getIsTest());
            dto.setOwner(bhToken.getApplyBrokerId() == brokerId);

            if (bhToken.getIsAggregate()) {
                dto.setAllowDeposit(detail.getAllowDeposit() && allowDepositWithdrawList.contains(detail.getTokenId()));
                dto.setAllowWithdraw(detail.getAllowWithdraw() && allowDepositWithdrawList.contains(detail.getTokenId()));
            }

            dtos.add(dto);
        }

//        if (!CollectionUtils.isEmpty(extraRequestInfos)) {
//            List<BaseConfigDTO> baseConfigs = new ArrayList<>();
//            for (ExtraRequestPO extraRequestPO : extraRequestInfos) {
//                BaseConfigPO po = new BaseConfigPO();
//                po.setOpPlatform(extraRequestPO.getOpPlatform());
//                po.setGroup(extraRequestPO.getGroup());
//                po.setKey(extraRequestPO.getKey());
//                po.setWithLanguage(false);
//                List<BaseConfigDTO> list = baseTokenConfigService.getConfigsByGroup(brokerId, po);
//                if (!CollectionUtils.isEmpty(list)) {
//                    baseConfigs.addAll(list);
//                }
//            }
//            for (TokenDTO dto : dtos) {
//                Map<String, String> extraMap = new HashMap<>();
//                for (BaseConfigDTO baseConfigDTO : baseConfigs) {
//                    if (baseConfigDTO.getIsOpen() && dto.getTokenId().equals(baseConfigDTO.getToken())) {
//                        extraMap.put(baseConfigDTO.getGroup() + "." + baseConfigDTO.getKey(), baseConfigDTO.getValue());
//                    }
//                }
//                dto.setExtra(extraMap);
//            }
//        }

        vo.setList(dtos);
        return vo;
    }

    @Override
    public PaginationVO<TokenDTO> queryToken(Integer current, Integer pageSize, Integer category, String tokenName, Long brokerId, List<ExtraRequestPO> extraRequestInfos) {
        QueryTokenReply reply = tokenClient.queryToken(current, pageSize, category, "", tokenName, brokerId);
        return queryToken(brokerId, reply);
    }

    @Override
    public List<TokenDTO> queryTokenByBrokerId(Integer current, Integer pageSize, Long brokerId) {
        QueryTokensReply reply = tokenClient.queryBrokerTokens(current, pageSize, StringUtils.EMPTY, brokerId);

        List<TokenDTO> dtos = new ArrayList<>();
        List<io.bhex.base.token.TokenDetail> tokens = reply.getTokenDetailsList();

        for (io.bhex.base.token.TokenDetail detail : tokens) {
            TokenDTO dto = new TokenDTO();
            BeanUtils.copyProperties(detail, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public Boolean allowDeposit(String tokenId, Boolean allowDeposit, Long brokerId) {
        return tokenClient.allowDeposit(tokenId, allowDeposit, brokerId);
    }

    @Override
    public Boolean allowWithdraw(String tokenId, Boolean allowWithdraw, Long brokerId) {
        return tokenClient.allowWithdraw(tokenId, allowWithdraw, brokerId);
    }

    @Override
    public Boolean publish(String tokenId, Boolean isPublish, Long brokerId) {
        return tokenClient.publish(tokenId, isPublish, brokerId);
    }

    @Override
    public void syncBhexTokens(Long brokerId) {
        tokenClient.syncBhexTokens(brokerId);
    }

    @Override
    public List<TokenDTO> listTokenByOrgId(Long orgId, Integer category) {
        io.bhex.broker.grpc.admin.QueryTokenReply reply = tokenClient.listTokenByOrgId(orgId, category);
        return reply.getTokenDetailsList().stream().map(i -> {
            return TokenDTO.builder()
                    .tokenId(i.getTokenId())
                    .tokenName(i.getTokenName())
                    .tokenFullName(i.getTokenFullName())
                    .allowDeposit(i.getAllowDeposit())
                    .allowWithdraw(i.getAllowWithdraw())
                    .depositMinQuantity(i.getDepositMinQuantity())
                    .minPrecision(i.getMinPrecision())
                    .build();
        }).collect(Collectors.toList());

    }

    @Override
    public io.bhex.base.token.TokenDetail getTokenFromBh(String tokenId, Long orgId) {
        GetTokenRequest request = GetTokenRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setTokenId(tokenId)
                .build();
        io.bhex.base.token.TokenDetail token = tokenClient.getToken(request);
        return token;
    }


//    private static Set<Long> syncTokenBrokers = new HashSet<>();
//
//    @Scheduled(initialDelay = 3000, fixedRate = 60_000)
//    public void syncBrokerTokens() {
//        List<Long> brokers = syncTokenBrokers.stream().collect(Collectors.toList());
//        brokers.forEach(brokerId -> {
//            tokenClient.syncBhexTokens(brokerId);
//            syncTokenBrokers.remove(brokerId);
//        });
//    }


    @Override
    public boolean setHighRiskToken(Long orgId, String tokenId, Boolean isHighRiskToken) {
        return tokenClient.setHighRiskToken(orgId, tokenId, isHighRiskToken);
    }

    @Override
    public boolean setWithdrawFee(Long orgId, String tokenId, BigDecimal withdrawFee) {
        return tokenClient.setWithdrawFee(orgId, tokenId, withdrawFee);
    }

    @Override
    public TokenApplyRecordDTO getApplyRecordById(long brokerId, Long applyRecordId) {
        QueryApplyTokenRecordRequest request = QueryApplyTokenRecordRequest.newBuilder()
                .setId(applyRecordId).setBrokerId(brokerId).build();
        TokenApplyObj applyRecord = tokenClient.queryApplyTokenRecord(request);

        return TokenApplyRecordDTO.parseTokenRecord(applyRecord);
    }

    @Override
    public TokenApplyRecordDTO getApplyRecordByTokenId(long brokerId, String tokenId) {
        QueryApplyTokenRecordRequest request = QueryApplyTokenRecordRequest.newBuilder()
                .setTokenId(tokenId).setBrokerId(brokerId).build();
        TokenApplyObj applyRecord = tokenClient.queryApplyTokenRecord(request);

        return TokenApplyRecordDTO.parseTokenRecord(applyRecord);
    }

    @Override
    public int applyToken(Long brokerId, TokenApplyPO tokenRecordPO, AdminTokenTypeEnum tokenTypeEnum) {

        ExchangeReply exchangeReply = orgClient.findExchangeByBrokerId(brokerId);
        if (exchangeReply == null || exchangeReply.getExchangeId() == 0) {
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }
        TokenApplyObj tokenRecord = tokenRecordPO.toTokenRecord(brokerId, exchangeReply.getExchangeId(), tokenTypeEnum);

        io.bhex.base.token.TokenDetail tokenDetail = getTokenFromBh(tokenRecordPO.getTokenId(), brokerId);
        if (tokenDetail != null && !tokenDetail.getTokenName().equals("")) {
            if (tokenRecord.getId() == 0) {
                throw new BizException(ErrorCode.TOKEN_ALREADY_EXIST);
            }

            //已经上过平台的币 不能再修改下面信息了
            tokenRecord = tokenRecord.toBuilder()
                    .setTokenName(tokenDetail.getTokenName())
                    .setTokenFullName(tokenDetail.getTokenFullName())
                    .setIsPrivateToken(tokenDetail.getPrivateTokenExchangeId() > 0)
                    .build();
        }
        return tokenClient.applyToken(tokenRecord);
    }

    @Override
    public PaginationVO<TokenApplyRecordDTO> listTokenApplyRecords(Long brokerId, Integer tokenType, Integer current, Integer pageSize) {
        TokenApplyRecordList tokenRecordList = tokenClient.listTokenApplyRecords(brokerId, tokenType, current, pageSize);

        PaginationVO<TokenApplyRecordDTO> paginationVO = new PaginationVO<>();
        paginationVO.setList(tokenRecordList.getApplyRecordList().stream()
                .map(TokenApplyRecordDTO::parseTokenRecord)
                .collect(Collectors.toList()));
        paginationVO.setCurrent(current);
        paginationVO.setPageSize(pageSize);
        paginationVO.setTotal(tokenRecordList.getTotal());
        return paginationVO;
    }

    @Override
    public AdminSimplyReply editTokenName(long orgId, String tokenId, String newTokenName) {
        return tokenClient.editTokenName(orgId, tokenId, newTokenName);
    }

    @Override
    public AdminSimplyReply editTokenFullName(long orgId, String tokenId, String newTokenFullName) {
        return tokenClient.editTokenFullName(orgId, tokenId, newTokenFullName);
    }

    @Override
    public AdminSimplyReply editTokenExtraTags(long orgId, String tokenId, Map<String, Integer> tagMap) {
        return tokenClient.editTokenExtraTags(orgId, tokenId, tagMap);
    }

    @Override
    public AdminSimplyReply editTokenExtraConfigs(long orgId, String tokenId, Map<String, String> configMap) {
        return tokenClient.editTokenExtraConfigs(orgId, tokenId, configMap);
    }

    @Override
    public void editTokenTaskConfig(Long orgId, AdminUserReply adminUserReply, BrokerTokenTaskConfigDTO po) {

        brokerTaskConfigService.editTokenTaskConfig(orgId,adminUserReply,po);

    }

    @Override
    public List<BrokerTokenTaskConfigDTO> getTokenTaskConfigs(Long orgId, Integer pageSize, Long fromId){
       return brokerTaskConfigService.getTokenTaskConfigs(orgId,pageSize,fromId);
    }
}
