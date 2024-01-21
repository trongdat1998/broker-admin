package io.bhex.broker.admin.grpc.client.impl;

import com.google.common.base.Strings;
import io.bhex.base.account.ExchangeReply;
import io.bhex.base.bhadmin.*;
import io.bhex.base.token.*;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.controller.dto.SymbolApplyRecordDTO;
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.SymbolDetail;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.common.AdminSimplyReply;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
public class SymbolClientImpl implements SymbolClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Autowired
    private OrgClient orgClient;

    private AdminSymbolServiceGrpc.AdminSymbolServiceBlockingStub getSymbolStub() {
        return grpcConfig.adminSymbolServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private SymbolServiceGrpc.SymbolServiceBlockingStub getBhexSymbolStub() {
        return grpcConfig.symbolServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
    }

    private SymbolServiceGrpc.SymbolServiceBlockingStub getBhSymbolStub() {
        return grpcConfig.symbolServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME);
    }

    AdminSymbolApplyServiceGrpc.AdminSymbolApplyServiceBlockingStub applyStub() {
        return grpcConfig.adminSymbolApplyServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
    }

    AdminSymbolTransferServiceGrpc.AdminSymbolTransferServiceBlockingStub symbolTransferStub() {
        return grpcConfig.adminSymbolTransferServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
    }

    @Override
    public io.bhex.base.token.SymbolDetail getBhSymbolInfo(GetSymbolRequest request) {
        SymbolServiceGrpc.SymbolServiceBlockingStub stub = grpcConfig.symbolServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME);
        return stub.getSymbol(request);
    }

    @Override
    public List<ExchangeSymbolDetail> queryBhExchangeSymbols(Long exchangeId, List<String> symbolIds) {
        QueryExchangeSymbolsByIdsRequest request = QueryExchangeSymbolsByIdsRequest.newBuilder()
                .setExchangeId(exchangeId)
                .addAllSymbolIds(symbolIds)
                .build();
        QueryExchangeSymbolsByIdsReply bhreply = getBhSymbolStub().queryExchangeSymbolsByIds(request);
        List<ExchangeSymbolDetail> list = bhreply.getExchangeSymbolDetailsList();
        return list;
    }

    @Override
    public SymbolDetail queryBrokerSymbolById(long brokerId, String symbolId) {
        QueryOneSymbolRequest request = QueryOneSymbolRequest.newBuilder().setBrokerId(brokerId).setSymbolId(symbolId).build();
        return getSymbolStub().queryOneSymbol(request);
    }

    @Override
    public QuerySymbolReply querySymbol(Integer current, Integer pageSize, Integer category, String quoteToken,
                                        String symbolName, Long brokerId, Long exchangeId, List<String> symbols) {
        if (StringUtils.isEmpty(symbolName)) {
            symbolName = new String();
        }
        QuerySymbolRequest.Builder builder = QuerySymbolRequest.newBuilder()
                .setCurrent(current)
                .setPageSize(pageSize);
        if (!StringUtils.isEmpty(symbolName)) {
            builder.setSymbolName(symbolName.replace("/", ""));
        }
        if (!StringUtils.isEmpty(quoteToken)) {
            builder.setQuoteToken(quoteToken);
        }
        if (category != null && category > 0) {
            builder.setCategory(category);
        }
        if (brokerId != null && brokerId > 0) {
            builder.setBrokerId(brokerId);
        }
        if (exchangeId != null && exchangeId > 0) {
            builder.setExchangeId(exchangeId);
        }
        if (!CollectionUtils.isEmpty(symbols)) {
            builder.addAllSymbolId(symbols);
        }
        return getSymbolStub().querySymbol(builder.build());
    }

    @Override
    public Boolean allowTrade(Long exchangeId, String symbolId, Boolean allowTrade, Long brokerId) {
        SymbolAllowTradeRequest request = SymbolAllowTradeRequest.newBuilder()
                .setExchangeId(exchangeId)
                .setSymbolId(symbolId)
                .setAllowTrade(allowTrade)
                .setBrokerId(brokerId)
                .build();
        return getSymbolStub().symbolAllowTrade(request).getResult();
    }

    @Override
    public Boolean publish(String symbolId, Boolean isPublished, Long brokerId) {
        if (!isPublished) { //关闭币对
            CloseSymbolRequest closeSymbolRequest = CloseSymbolRequest.newBuilder().setBrokerId(brokerId).setSymbolId(symbolId).build();
            CloseSymbolResult closeSymbolResult = applyStub().closeSymbol(closeSymbolRequest);
            if (closeSymbolResult.getRes() != 0) {
                throw new BizException(closeSymbolResult.getMessage());
            }
        } else {
            OpenSymbolRequest openSymbolRequest = OpenSymbolRequest.newBuilder().setBrokerId(brokerId).setSymbolId(symbolId).build();
            OpenSymbolResult openSymbolResult = applyStub().openSymbol(openSymbolRequest);
            if (openSymbolResult.getRes() != 0) {
                throw new BizException(openSymbolResult.getMessage());
            }
        }

        SymbolPublishRequest request = SymbolPublishRequest.newBuilder()
                .setSymbolId(symbolId)
                .setPublish(isPublished)
                .setBrokerId(brokerId)
                .build();
        return getSymbolStub().symbolPublish(request).getResult();
    }

    @Override
    public SymbolAgencyReply agencySymbol(Long exchangeId, List<String> symbolNames, Long brokerId) {
        SymbolAgencyRequest request = SymbolAgencyRequest.newBuilder()
                .setExchangeId(exchangeId)
                .addAllSymbolId(symbolNames)
                .setBrokerId(brokerId)
                .build();

        return getSymbolStub().symbolAgency(request);
    }

    @Override
    public QueryBrokerExchangeSymbolsReply queryExchangeSymbolsByBrokerId(Long exchangeId, Long brokerId, Integer current, Integer pageSize, Integer category) {
        QueryBrokerExchangeSymbolsRequest request = QueryBrokerExchangeSymbolsRequest.newBuilder()
                .setBrokerId(brokerId)
                .setExchangeId(exchangeId)
                .setCurrent(current)
                .setPageSize(pageSize)
                .setCategory(category)
                .build();
        return getBhexSymbolStub().queryBrokerExchangeSymbols(request);
    }

    @Override
    public QueryExistSymbolReply queryExistSymbol(Long exchangeId, Long brokerId, List<String> symbolIds) {
        QueryExistSymbolRequest request = QueryExistSymbolRequest.newBuilder()
                .setBrokerId(brokerId)
                .setExchangeId(exchangeId)
                .addAllSymbolId(symbolIds)
                .build();
        QueryExistSymbolReply reply = getSymbolStub().queryExistSymbol(request);
//        log.info("query exist symbol. Reply : " + reply.getTotal());
        return reply;
    }

    @Override
    public Boolean banSale(Long exchangeId, String symbolId, Boolean ban, Long brokerId) {
        SymbolBanTypeRequest request = SymbolBanTypeRequest.newBuilder()
                .setExchangeId(exchangeId)
                .setSymbolId(symbolId)
                .setBanSellStatus(ban)
                .setBrokerId(brokerId)
                .build();
        SymbolBanSellStatusReply reply = getSymbolStub().symbolUpdateBanSellStatus(request);
        log.info("request:{} response:{}", request, reply);
        return reply.getResult();
    }

    @Override
    public Boolean setBanSaleWhiteList(Long brokerId, Long accountId, String username) {
        SymbolWhiteListRequest request = SymbolWhiteListRequest.newBuilder()
                .setBrokerId(brokerId)
                .addSaleWhite(SaleWhite.newBuilder().setAccountId(accountId).setUserName(username).build())
                .build();
        SymbolWhiteAccountIdListReply reply = getSymbolStub().symbolUpdateWhiteAccountIdList(request);
        log.info("request:{} response:{}", request, reply);
        return reply.getResult();
    }

    @Override
    public Combo2<Long, String> getBanSaleWhiteAccountId(Long brokerId) {
        QueryWhiteAccountIdListRequest request = QueryWhiteAccountIdListRequest.newBuilder().setOrgId(brokerId).build();

        QueryWhiteAccountIdListRequestReply reply = getSymbolStub().queryWhiteAccountIdList(request);


        List<io.bhex.broker.grpc.admin.SaleWhite> whitelist = reply.getSaleWhiteList();
        if (CollectionUtils.isEmpty(whitelist)) {
            return null;
        }

        return new Combo2<>(whitelist.get(0).getAccountId(), whitelist.get(0).getUserName());
    }

    @Override
    public List<String> getRecommendSymbols(long orgId) {
        QueryRecommendSymbolsRequest request = QueryRecommendSymbolsRequest.newBuilder()
                .setBrokerId(orgId).build();
        QueryRecommendSymbolsReply reply = getSymbolStub().queryRecommendSymbols(request);
        return reply.getSymbolIdList();
    }

    @Override
    public boolean editRecommendSymbols(long orgId, List<String> symbols) {
        EditRecommendSymbolsRequest request = EditRecommendSymbolsRequest.newBuilder()
                .setBrokerId(orgId)
                .addAllSymbolId(symbols)
                .build();
        EditRecommendSymbolsReply reply = getSymbolStub().editRecommendSymbols(request);
        return reply.getResult();
    }


    @Override
    public boolean editSymbolSwitch(EditSymbolSwitchRequest request) {
        return getSymbolStub().editSymbolSwitch(request).getSuccess();
    }

    @Override
    public List<String> getQuoteSymbols(long orgId, String quoteTokenId, int category) {
        QueryQuoteSymbolsRequest request = QueryQuoteSymbolsRequest.newBuilder()
                .setBrokerId(orgId)
                .setTokenId(Strings.nullToEmpty(quoteTokenId))
                .setCategory(category)
                .build();
        QueryQuoteSymbolsReply reply = getSymbolStub().queryQuoteSymbols(request);
        return reply.getSymbolIdList();
    }

    @Override
    public boolean editQuoteSymbols(long orgId, String quoteTokenId, List<String> symbols, int category) {
        EditQuoteSymbolsRequest request = EditQuoteSymbolsRequest.newBuilder()
                .setBrokerId(orgId)
                .setTokenId(Strings.nullToEmpty(quoteTokenId))
                .addAllSymbolId(symbols)
                .setCategory(category)
                .build();
        EditQuoteSymbolsReply reply = getSymbolStub().editQuoteSymbols(request);
        return reply.getResult();
    }

    @Override
    public boolean editQuoteTokens(long orgId, List<String> quoteTokens) {
        EditQuoteTokensRequest request = EditQuoteTokensRequest.newBuilder()
                .setBrokerId(orgId)
                .addAllTokenId(quoteTokens)
                .build();
        EditQuoteTokensReply reply = getSymbolStub().editQuoteTokens(request);
        return reply.getResult();
    }

    @Override
    public boolean editSymbolFilterTime(long orgId, String symbolId, long filterTime) {
        EditSymbolFilterTimeRequest request = EditSymbolFilterTimeRequest.newBuilder()
                .setBrokerId(orgId)
                .setFilterTime(filterTime)
                .setSymbolId(symbolId)
                .build();
        EditSymbolFilterTimeReply reply = getSymbolStub().editSymbolFilterTime(request);
        return reply.getResult();
    }

    @Override
    public List<String> queryFuturesCoinToken(long orgId) {
        QueryFuturesCoinTokenRequest request = QueryFuturesCoinTokenRequest.newBuilder()
                .setBrokerId(orgId)
                .build();
        QueryFuturesCoinTokenReply reply = getSymbolStub().queryFuturesCoinToken(request);
        return reply.getFuturesCoinTokenList();
    }

    @Override
    public boolean editSymbolCustomLabel(Long orgId, String symbolId, Long labelId) {
        SetSymbolLabelRequest request = SetSymbolLabelRequest.newBuilder()
                .setOrgId(orgId)
                .setSymbolId(symbolId)
                .setLabelId(labelId)
                .build();
        return getSymbolStub().setSymbolLabel(request).getResult();
    }

    @Override
    public boolean hideFromOpenapi(Long orgId, String symbolId, Boolean hideFromOpenapi) {
        HideFromOpenapiRequest request = HideFromOpenapiRequest.newBuilder()
                .setOrgId(orgId)
                .setSymbolId(symbolId)
                .setHideFromOpenapi(hideFromOpenapi)
                .build();
        return getSymbolStub().hideFromOpenapi(request).getResult();
    }

    @Override
    public boolean forbidOpenapiTrade(Long orgId, String symbolId, Boolean forbidOpenapiTrade) {
        ForbidOpenapiTradeRequest request = ForbidOpenapiTradeRequest.newBuilder()
                .setOrgId(orgId)
                .setSymbolId(symbolId)
                .setForbidOpenapiTrade(forbidOpenapiTrade)
                .build();
        return getSymbolStub().forbidOpenapiTrade(request).getResult();
    }

    @Override
    public int applySymbol(SymbolApplyObj applyObj) {
        ExchangeReply exchangeReply = orgClient.findExchangeByBrokerId(applyObj.getBrokerId());
        applyObj = applyObj.toBuilder().setExchangeId(exchangeReply.getExchangeId()).build();
        io.bhex.base.token.SymbolDetail symbolDetail = null;
        if (applyObj.getId() == 0) {
            try {
                symbolDetail = getBhSymbolInfo(GetSymbolRequest.newBuilder()
                        .setSymbolId(applyObj.getSymbolId())
                        .build());
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            if (Objects.nonNull(symbolDetail)) {
                throw new BizException(ErrorCode.SYMBOL_ALREADY_EXIST);
            }
        }
        ApplySymbolResult res = applyStub().applySymbol(applyObj);
        int result = res.getRes();
        if (result == -1) {
            throw new BizException(ErrorCode.SYMBOL_ALREADY_EXIST);
        }
        if (result == -2) {
            throw new BizException(ErrorCode.FORBIDDEN_EDIT);
        }
        return result;
    }

    @Override
    public PaginationVO<SymbolApplyRecordDTO> listSymbolRecordList(Long brokerId, Integer current, Integer pageSize) {
        GetSymbolPager pager = GetSymbolPager.newBuilder()
                .setBrokerId(brokerId)
                .setStart(current)
                .setSize(pageSize)
                .setState(-1)
                .build();

        SymbolApplyRecordList list = applyStub().listApplyRecords(pager);
        PaginationVO<SymbolApplyRecordDTO> resultPagination = new PaginationVO<>();
        resultPagination.setList(list.getSymbolRecordList()
                .stream()
                .map(SymbolApplyRecordDTO::parseSymbolRecord)
                .collect(Collectors.toList()));
        resultPagination.setCurrent(current);
        resultPagination.setTotal(list.getTotal());
        resultPagination.setPageSize(pageSize);
        return resultPagination;
    }

    @Override
    public QueryUpdatingSymbolsResult getUpdatingSymbols(long brokerId, List<String> symbolIdList) {
        QueryUpdatingSymbolsRequest request = QueryUpdatingSymbolsRequest.newBuilder()
                .setBrokerId(brokerId)
                .addAllSymbolId(symbolIdList)
                .build();
        return applyStub().queryUpdatingSymbols(request);
    }

    @Override
    public AdminSimplyReply editSymbolExtraTags(long orgId, String symbolId, Map<String, Integer> tagMap) {
        EditSymbolExtraTagsRequest request = EditSymbolExtraTagsRequest.newBuilder()
                .setOrgId(orgId)
                .setSymbolId(symbolId)
                .putAllExtraTag(tagMap)
                .build();
        return getSymbolStub().editSymbolExtraTags(request);
    }

    @Override
    public AdminSimplyReply editSymbolExtraConfigs(long orgId, String symbolId, Map<String, String> configMap) {
        EditSymbolExtraConfigsRequest request = EditSymbolExtraConfigsRequest.newBuilder()
                .setOrgId(orgId)
                .setSymbolId(symbolId)
                .putAllExtraConfig(configMap)
                .build();
        return getSymbolStub().editSymbolExtraConfigs(request);
    }

    @Override
    public SaveSymbolTransferReply saveSymbolTransfer(SaveSymbolTransferRequest request) {
        return symbolTransferStub().saveSymbolTransfer(request);
    }

    @Override
    public SaveSymbolTransferReply closeSymbolTransfer(CloseSymbolTransferRequest request) {
        return symbolTransferStub().closeSymbolTransfer(request);
    }

    @Override
    public ListSymbolTransferReply listSymbolTransferBySymbolIds(ListSymbolTransferRequest request) {
        return symbolTransferStub().listSymbolTransferBySymbolIds(request);
    }
}
