package io.bhex.broker.admin.service;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.token.QueryBrokerExchangeSymbolsReply;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.CustomerQuoteDTO;
import io.bhex.broker.admin.controller.dto.QuerySymbolsByExchangeDTO;
import io.bhex.broker.admin.controller.dto.SymbolDTO;
import io.bhex.broker.admin.controller.param.EditCustomerQuoteSymbolsPO;
import io.bhex.broker.admin.controller.param.ExtraRequestPO;
import io.bhex.broker.admin.controller.param.SymbolMatchTransferPO;
import io.bhex.broker.admin.controller.param.SymbolSwitchPO;
import io.bhex.broker.grpc.admin.EditSymbolSwitchRequest;
import io.bhex.broker.grpc.admin.SymbolAgencyReply;
import io.bhex.broker.grpc.admin.SymbolDetail;
import io.bhex.broker.grpc.common.AdminSimplyReply;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 11:55 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface SymbolService {

    PaginationVO<SymbolDTO> querySymbol(Integer current, Integer pageSize, Integer category,
                                        String quoteToken, String symbolName, Long brokerId, List<ExtraRequestPO> extraRequestInfos, String customerQuoteId);

    SymbolDetail queryBrokerSymbolById(long brokerId, String symbolId);

    Boolean allowTrade(Long exchangeId, String symbolId, Boolean allowTrade, Long brokerId);

    Boolean publish(String symbolId, Boolean isPublished, Long brokerId);

    SymbolAgencyReply agencySymbol(Long exchangeId, List<String> symbolNames, Long brokerId);

    Boolean banSale(Long exchangeId, String symbolId, Boolean ban, Long brokerId);

    Boolean setBanSaleWhiteList(Long brokerId, Long accountId, String username);

    Combo2<Long, String> getBanSaleWhiteAccountId(Long brokerId);

    PaginationVO<QuerySymbolsByExchangeDTO> queryExchangeSymbolsByBrokerId(Long exchangeId, Long brokerId, Integer current, Integer pageSize, Integer category);

    List<String> getRecommendSymbols(long orgId);

    boolean editRecommendSymbols(long orgId, List<String> symbols);

    QueryBrokerExchangeSymbolsReply queryBhExchangeSymbolsByBrokerId(Long exchangeId, Long brokerId, Integer current, Integer pageSize, Integer category);

    boolean availableInExchange(Long brokerId, String symbolId);

    boolean editSymbolSwitch(long brokerId, SymbolSwitchPO po, String extraInfo);

    List<String> getQuoteSymbols(long orgId, String quoteTokenId, int category);

    boolean editQuoteSymbols(long orgId, String quoteTokenId, List<String> symbols, int category);

    boolean editQuoteTokens(long orgId, List<String> quoteTokens);

    boolean editSymbolFilterTime(long orgId, String symbolId, long filterTime);

    CustomerQuoteDTO queryCustomerQuoteTokens(long orgId);

    //新建券商初始化交易区
    void initCustomerQuoteTokens(long brokerId, AdminUserReply adminUser);

    boolean editCustomerQuoteTokens(long orgId, CustomerQuoteDTO customerQuoteDTO, AdminUserReply adminUser);

    boolean editCustomerQuoteSymbols(long orgId, EditCustomerQuoteSymbolsPO po, AdminUserReply adminUser);

    List<String> getCustomerQuoteSymbols(long orgId, String customerQuoteId);

    boolean editSymbolCustomLabel(long orgId, String symbolId, long labelId);

    boolean hideFromOpenapi(Long orgId, String symbolId, Boolean hideFromOpenapi);

    boolean forbidOpenapiTrade(Long orgId, String symbolId, Boolean forbidOpenapiTrade);

    ResultModel createSymbolTransfer(Long orgId, SymbolMatchTransferPO po);

    ResultModel closeSymbolTransfer(long orgId, String symbolId);

    AdminSimplyReply editSymbolExtraTags(long orgId, String symbolId, Map<String, Integer> tagMap);

    AdminSimplyReply editSymbolExtraConfigs(long orgId, String symbolId, Map<String, String> configMap);

}
