package io.bhex.broker.admin.grpc.client;

import io.bhex.base.bhadmin.*;
import io.bhex.base.token.ExchangeSymbolDetail;
import io.bhex.base.token.GetSymbolRequest;
import io.bhex.base.token.QueryBrokerExchangeSymbolsReply;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.controller.dto.SymbolApplyRecordDTO;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.common.AdminSimplyReply;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 5:17 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface SymbolClient {

    io.bhex.base.token.SymbolDetail getBhSymbolInfo(GetSymbolRequest request);

    List<ExchangeSymbolDetail> queryBhExchangeSymbols(Long exchangeId, List<String> symbolIds);

    QuerySymbolReply querySymbol(Integer current, Integer pageSize, Integer category, String quoteToken, String symbolName, Long brokerId, Long exchangeId, List<String> symbols);

    SymbolDetail queryBrokerSymbolById(long brokerId, String symbolId);

    Boolean allowTrade(Long exchangeId, String symbolId, Boolean allowTrade, Long brokerId);

    Boolean publish(String symbolId, Boolean isPublished, Long brokerId);

    SymbolAgencyReply agencySymbol(Long exchangeId, List<String> symbolNames, Long brokerId);

    QueryBrokerExchangeSymbolsReply queryExchangeSymbolsByBrokerId(Long exchangeId, Long brokerId, Integer current, Integer pageSize, Integer category);

    QueryExistSymbolReply queryExistSymbol(Long exchangeId, Long brokerId, List<String> symbolIds);

    Boolean banSale(Long exchangeId, String symbolId, Boolean ban, Long brokerId);

    Boolean setBanSaleWhiteList(Long brokerId, Long accountId, String username);

    Combo2<Long, String> getBanSaleWhiteAccountId(Long brokerId);

    List<String> getRecommendSymbols(long orgId);

    boolean editRecommendSymbols(long orgId, List<String> symbols);

    boolean editSymbolSwitch(EditSymbolSwitchRequest request);

    List<String> getQuoteSymbols(long orgId, String quoteTokenId, int category);

    boolean editQuoteSymbols(long orgId, String quoteTokenId, List<String> symbols, int category);

    boolean editQuoteTokens(long orgId, List<String> quoteTokens);

    /**
     * 修改币对k线显示时间
     * @param orgId
     * @param symbolId
     * @param filterTime
     * @return
     */
    boolean editSymbolFilterTime(long orgId, String symbolId, long filterTime);

    List<String> queryFuturesCoinToken(long orgId);

    boolean editSymbolCustomLabel(Long orgId, String symbolId, Long labelId);

    boolean hideFromOpenapi(Long orgId, String symbolId, Boolean hideFromOpenapi);

    boolean forbidOpenapiTrade(Long orgId, String symbolId, Boolean forbidOpenapiTrade);

    int applySymbol(SymbolApplyObj applyObj);

    PaginationVO<SymbolApplyRecordDTO> listSymbolRecordList(Long brokerId, Integer current, Integer pageSize);

    QueryUpdatingSymbolsResult getUpdatingSymbols(long brokerId, List<String> symbolIdList);

    AdminSimplyReply editSymbolExtraTags(long orgId, String symbolId, Map<String, Integer> tagMap);

    AdminSimplyReply editSymbolExtraConfigs(long orgId, String symbolId, Map<String, String> configMap);

    SaveSymbolTransferReply saveSymbolTransfer(SaveSymbolTransferRequest request);

    SaveSymbolTransferReply closeSymbolTransfer(CloseSymbolTransferRequest request);

    ListSymbolTransferReply listSymbolTransferBySymbolIds(ListSymbolTransferRequest request);

}
