package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.protobuf.TextFormat;
import io.bhex.base.admin.common.AccountType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.bhadmin.*;
import io.bhex.base.common.EditReply;
import io.bhex.base.exadmin.ListSymbolMatchTransferReply;
import io.bhex.base.exadmin.ListSymbolMatchTransferRequest;
import io.bhex.base.exadmin.SymbolMatchTransferInfo;
import io.bhex.base.idgen.api.ISequenceGenerator;
import io.bhex.base.token.ExchangeSymbolDetail;
import io.bhex.base.token.QueryBrokerExchangeSymbolsReply;
import io.bhex.base.token.QueryExchangeSymbolsByIdsReply;
import io.bhex.base.token.QueryExchangeSymbolsByIdsRequest;
import io.bhex.bhop.common.config.OrgInstanceConfig;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.dto.param.BrokerInstanceRes;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.BrokerClient;
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.service.SymbolService;
import io.bhex.broker.admin.service.TokenService;
import io.bhex.broker.common.util.ExtraConfigUtil;
import io.bhex.broker.common.util.ExtraTagUtil;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.common.AdminSimplyReply;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
public class SymbolServiceImpl implements SymbolService {

    private static final String SYMBOL_NAME_TEMPLATE = "%s/%s";

    @Autowired
    private SymbolClient symbolClient;
    @Autowired
    @Qualifier(value = "baseSymbolConfigService")
    private BaseConfigService baseSymbolConfigService;
    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;
    @Resource
    private ISequenceGenerator sequenceGenerator;


    @Override
    public PaginationVO<SymbolDTO> querySymbol(Integer current, Integer pageSize, Integer category, String quoteToken,
                                               String symbolName, Long brokerId, List<ExtraRequestPO> extraRequestInfos, String customerQuoteId) {
        List<String> symbols = new ArrayList<>();
        if (StringUtils.isNotEmpty(customerQuoteId)) {
            symbols = getCustomerQuoteSymbols(brokerId, customerQuoteId);
            if (CollectionUtils.isEmpty(symbols)) {
                PaginationVO<SymbolDTO> vo = new PaginationVO();
                vo.setCurrent(current);
                vo.setPageSize(pageSize);
                vo.setTotal(0);
                vo.setList(new ArrayList<>());
                return vo;
            }
            symbols = symbols.stream().map(s -> s.replace("/", "")).collect(Collectors.toList());
        }
        QuerySymbolReply reply = symbolClient.querySymbol(current, pageSize, category, quoteToken, symbolName, brokerId, null, symbols);
        PaginationVO<SymbolDTO> vo = new PaginationVO();
        BeanUtils.copyProperties(reply, vo);
        if (CollectionUtils.isEmpty(reply.getSymbolDetailsList())) {
            return vo;
        }

        List<SymbolDTO> dtos = new ArrayList<>();
        List<SymbolDetail> symbolDetailsList = reply.getSymbolDetailsList();
        List<String> symbolIdList = symbolDetailsList.stream().map(s -> s.getSymbolId()).distinct().collect(Collectors.toList());
        Map<String, SymbolTransferInfo> transferInfoMap = mapSymbolTransferInfo(brokerId, symbolIdList);
        QueryUpdatingSymbolsResult updatingSymbolsResult = symbolClient.getUpdatingSymbols(brokerId, symbolIdList);
        for (SymbolDetail detail : symbolDetailsList) {
            SymbolDTO dto = new SymbolDTO();
            BeanUtils.copyProperties(detail, dto);
            dto.setSymbolName(String.format(SYMBOL_NAME_TEMPLATE, detail.getBaseTokenName(), detail.getQuoteTokenName()));
            dto.setDepthMerge(detail.getDigitMergeList());
            dto.setLabelId(detail.getLabelId());
            dto.setHideFromOpenapi(detail.getHideFromOpenapi());
            dto.setForbidOpenapiTrade(detail.getForbidOpenapiTrade());
            dto.setTags(ExtraTagUtil.newInstance(detail.getExtraTagMap()).map());
            dto.setConfigs(ExtraConfigUtil.newInstance(detail.getExtraConfigMap()).map());
            dto.setUpdatingStatus(updatingSymbolsResult.getResultMap().getOrDefault(detail.getSymbolId(), 0));
            if (transferInfoMap.containsKey(detail.getSymbolId())) {
                SymbolTransferInfo transferInfo = transferInfoMap.get(detail.getSymbolId());
                dto.setTransferStatus(transferInfo.getEnable());
                dto.setTransferBrokerName(transferInfo.getMatchBrokerName());
            } else {
                dto.setTransferStatus(0);
            }

            dtos.add(dto);
        }

        vo.setList(dtos);
        return vo;
    }


    private Map<String, SymbolTransferInfo> mapSymbolTransferInfo(Long brokerId, List<String> symbolIds) {
        Map<String, SymbolTransferInfo> transferInfoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(symbolIds)) {
            ListSymbolTransferRequest request = ListSymbolTransferRequest.newBuilder()
                    .setSourceBrokerId(brokerId)
                    .addAllSymbolIds(symbolIds)
                    .build();
            ListSymbolTransferReply reply = symbolClient.listSymbolTransferBySymbolIds(request);
            transferInfoMap = reply.getSymbolTransferInfoList().stream()
                    .collect(Collectors.toMap(SymbolTransferInfo::getSymbolId, Function.identity()));
        }
        return transferInfoMap;
    }

    @Override
    public SymbolDetail queryBrokerSymbolById(long brokerId, String symbolId) {
        return symbolClient.queryBrokerSymbolById(brokerId, symbolId);
    }

    @Override
    public Boolean allowTrade(Long exchangeId, String symbolId, Boolean allowTrade, Long brokerId) {
        return symbolClient.allowTrade(exchangeId, symbolId, allowTrade, brokerId);
    }

    @Override
    public Boolean publish(String symbolId, Boolean isPublished, Long brokerId) {
        boolean publishSuc = symbolClient.publish(symbolId, isPublished, brokerId);
        if (!publishSuc) {
            log.info("brokerId:{} symbolId:{} publish false", brokerId, symbolId);
            return false;
        }
        return publishSuc;
    }

    @Override
    public SymbolAgencyReply agencySymbol(Long exchangeId, List<String> symbolNames, Long brokerId) {
        return symbolClient.agencySymbol(exchangeId, symbolNames, brokerId);
    }

    @Override
    public Boolean banSale(Long exchangeId, String symbolId, Boolean ban, Long brokerId) {
        return symbolClient.banSale(exchangeId, symbolId, ban, brokerId);
    }

    @Override
    public Boolean setBanSaleWhiteList(Long brokerId, Long accountId, String username) {
        return symbolClient.setBanSaleWhiteList(brokerId, accountId, username);
    }

    @Override
    public Combo2<Long, String> getBanSaleWhiteAccountId(Long brokerId) {
        return symbolClient.getBanSaleWhiteAccountId(brokerId);
    }

    @Override
    public QueryBrokerExchangeSymbolsReply queryBhExchangeSymbolsByBrokerId(Long exchangeId, Long brokerId, Integer current, Integer pageSize, Integer category) {
        QueryBrokerExchangeSymbolsReply reply = symbolClient.queryExchangeSymbolsByBrokerId(exchangeId, brokerId, current, pageSize, category);
        return reply;
    }

    @Override
    public boolean availableInExchange(Long brokerId, String symbolId) {
        SymbolDetail brokerSymbol = symbolClient.queryBrokerSymbolById(brokerId, symbolId);
        List<ExchangeSymbolDetail> details = symbolClient.queryBhExchangeSymbols(brokerSymbol.getExchangeId(), Lists.newArrayList(symbolId));
        if (CollectionUtils.isEmpty(details)) {
            log.warn("{} {} not availableInExchange", brokerId, symbolId);
            return false;
        }
        details.forEach(d -> log.info("{}", TextFormat.shortDebugString(d)));
        return details.stream().anyMatch(s -> s.getPublished() && s.getSymbolId().equals(symbolId));
    }

    @Override
    public PaginationVO<QuerySymbolsByExchangeDTO> queryExchangeSymbolsByBrokerId(Long exchangeId, Long brokerId, Integer current, Integer pageSize, Integer category) {
        QueryBrokerExchangeSymbolsReply reply = symbolClient.queryExchangeSymbolsByBrokerId(exchangeId, brokerId, current, pageSize, category);
//        log.info("BH gRpc queryExchangeSymbolsByBrokerId. Reply : {}", reply);
        PaginationVO<QuerySymbolsByExchangeDTO> vo = new PaginationVO();
        BeanUtils.copyProperties(reply, vo);

        List<QuerySymbolsByExchangeDTO> dtos = new ArrayList<>();
        List<ExchangeSymbolDetail> tokens = reply.getExchangeSymbolDetailsList();
        List<String> symbolIds = new ArrayList<>();
        Map<String, QuerySymbolsByExchangeDTO> dtoMap = new HashMap<>();
        for (ExchangeSymbolDetail detail : tokens) {
            if (!detail.getPublished()) { //如果交易所未上线 不展示给券商
                continue;
            }
            QuerySymbolsByExchangeDTO dto = new QuerySymbolsByExchangeDTO();
            dto.setStatus(QuerySymbolsByExchangeDTO.NOT_EXIST_STATUS);
            BeanUtils.copyProperties(detail, dto);
            dto.setExchangeId(exchangeId);
            dto.setSymbolName(String.format(SYMBOL_NAME_TEMPLATE, detail.getBaseTokenId(), detail.getQuoteTokenId()));
//            log.info("BH gRpc queryExchangeSymbolsByBrokerId. symbolId : {}, baseTokenId : {}, quoteTokenId : {}", detail.getSymbolId(), detail.getBaseTokenId(), detail.getQuoteTokenId());
            dtos.add(dto);
            dtoMap.put(dto.getSymbolId(), dto);
            symbolIds.add(detail.getSymbolId());
        }
        QueryExistSymbolReply existSymbolReply = symbolClient.queryExistSymbol(exchangeId, brokerId, symbolIds);
//        log.info("Broker Server gRpc queryExistSymbol. Reply : {}", reply);
        if (0 != existSymbolReply.getTotal()) {
            List<SymbolDetail> symbolDetailList = existSymbolReply.getSymbolDetailList();
            for (SymbolDetail d : symbolDetailList) {
                QuerySymbolsByExchangeDTO dto = dtoMap.get(d.getSymbolId());
                if (null != dto) {
                    dto.setStatus(QuerySymbolsByExchangeDTO.EXIST_STATUS);
                }
            }
        }

        vo.setList(dtos);
        return vo;
    }

    @Override
    public List<String> getRecommendSymbols(long orgId) {
        return symbolClient.getRecommendSymbols(orgId);
    }

    @Override
    public boolean editRecommendSymbols(long orgId, List<String> symbols) {
        return symbolClient.editRecommendSymbols(orgId, symbols);
    }

    @Override
    public boolean editSymbolSwitch(long brokerId, SymbolSwitchPO po, String extraInfo) {
        EditSymbolSwitchRequest request = EditSymbolSwitchRequest.newBuilder()
                .setBrokerId(brokerId)
                //.setExchangeId(po.getExchangeId())
                .setOpen(po.getOpen())
                .setSymbolId(po.getSymbolId())
                .setSymbolSwitchValue(po.getSwitchType())
                .setExtraInfo(Strings.nullToEmpty(extraInfo))
                .build();
        return symbolClient.editSymbolSwitch(request);
    }

    @Override
    public List<String> getQuoteSymbols(long orgId, String quoteTokenId, int category) {
        return symbolClient.getQuoteSymbols(orgId, quoteTokenId, category);
    }

    @Override
    public boolean editQuoteSymbols(long orgId, String quoteTokenId, List<String> symbols, int category) {
        return symbolClient.editQuoteSymbols(orgId, quoteTokenId, symbols, category);
    }

    @Override
    public boolean editQuoteTokens(long orgId, List<String> quoteTokens) {
        return symbolClient.editQuoteTokens(orgId, quoteTokens);
    }

    @Override
    public boolean editSymbolFilterTime(long orgId, String symbolId, long filterTime) {
        log.info("editSymbolFilterTime:{},{},{}", orgId, symbolId, filterTime);
        return symbolClient.editSymbolFilterTime(orgId, symbolId, filterTime);
    }

    @Override
    public boolean editCustomerQuoteTokens(long orgId, CustomerQuoteDTO customerQuoteDTO, AdminUserReply adminUser) {
        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.CUSTOM_QUOTE_GROUP);
        configPO.setKey("quote.tabs." + "COIN");
        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(null);
        configPO.setWithLanguage(false);
        List<CustomerQuoteDTO.Item> items = customerQuoteDTO.getItems();
        items.forEach(i -> {
            if (StringUtils.isEmpty(i.getId())) {
                i.setId(sequenceGenerator.getLong().toString());
            }
        });

        configPO.setValue(JsonUtil.defaultGson().toJson(items));

        EditReply editReply = baseConfigService.editConfig(orgId, configPO, adminUser);

        return false;
    }

    private CustomerQuoteDTO getCustomerQuoteDTO(long orgId) {
        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.CUSTOM_QUOTE_GROUP);
        configPO.setKey("quote.tabs." + "COIN");

        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(null);
        configPO.setWithLanguage(false);

        BaseConfigDTO configDTO = baseConfigService.getOneConfig(orgId, configPO);
        if (configDTO == null || StringUtils.isEmpty(configDTO.getValue())) {
            return null;
        }
        String value = configDTO.getValue();
        log.info("value:{}", "{\"items\":" + value + "}");
        CustomerQuoteDTO dto = JsonUtil.defaultGson().fromJson("{\"items\":" + value + "}", CustomerQuoteDTO.class);
        return dto;
    }

    @Override
    public CustomerQuoteDTO queryCustomerQuoteTokens(long orgId) {
        return getCustomerQuoteDTO(orgId);
    }

    @Override
    public boolean editCustomerQuoteSymbols(long orgId, EditCustomerQuoteSymbolsPO po, AdminUserReply adminUser) {
        CustomerQuoteDTO customerQuoteDTO = getCustomerQuoteDTO(orgId);
        if (customerQuoteDTO == null) {
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }
        boolean matchedId = customerQuoteDTO.getItems().stream().anyMatch(c -> c.getId().equals(po.getCustomerQuoteId()));
        if (!matchedId) {
            log.warn("error customerQuoteId : {} org:{}", po.getCustomerQuoteId(), orgId);
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }

        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.CUSTOM_QUOTE_GROUP);
        configPO.setKey(po.getCustomerQuoteId() + "");
        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(null);
        configPO.setWithLanguage(false);
        configPO.setValue(JsonUtil.defaultGson().toJson(po.getSymbols()));
        EditReply editReply = baseConfigService.editConfig(orgId, configPO, adminUser);

        return true;
    }

    @Override
    public List<String> getCustomerQuoteSymbols(long orgId, String customerQuoteId) {
        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.CUSTOM_QUOTE_GROUP);
        configPO.setKey(customerQuoteId);
        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(null);
        configPO.setWithLanguage(false);

        BaseConfigDTO configDTO = baseConfigService.getOneConfig(orgId, configPO);
        if (configDTO == null || StringUtils.isEmpty(configDTO.getValue())) {
            return new ArrayList<>();
        }
        String value = configDTO.getValue();

        return JsonUtil.defaultGson().fromJson(value, List.class);
    }

    @Override
    public void initCustomerQuoteTokens(long brokerId, AdminUserReply adminUser) {
        List<String> quoteTokens = Lists.newArrayList("BTC", "USDT", "ETH");
        CustomerQuoteDTO customerQuoteDTO = new CustomerQuoteDTO();
        List<CustomerQuoteDTO.Item> items = new ArrayList<>();
        for (String quoteToken : quoteTokens) {
            CustomerQuoteDTO.Item item = new CustomerQuoteDTO.Item();
            String id = sequenceGenerator.getLong().toString();
            item.setId(id);

            List<CustomerQuoteDTO.ContentData> contentDataList = new ArrayList<>();
            List<String> languages = Lists.newArrayList("en_US", "zh_CN");
            for (String language : languages) {
                CustomerQuoteDTO.ContentData contentData = new CustomerQuoteDTO.ContentData();
                contentData.setEnable(true);
                contentData.setLocale(language);
                contentData.setTabName(quoteToken);
                contentDataList.add(contentData);
            }
            item.setContentlist(contentDataList);
            items.add(item);
        }
        customerQuoteDTO.setItems(items);

        editCustomerQuoteTokens(brokerId, customerQuoteDTO, adminUser);
    }

    @Autowired
    private OrgInstanceConfig orgInstanceConfig;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private BrokerClient brokerClient;


    @Scheduled(cron = "25 7 1 * * ?")
    public void initBrokerCustomerQuotes() {
        List<BrokerInstanceRes> brokers = orgInstanceConfig.listBrokerInstances();
        for (BrokerInstanceRes broker : brokers) {
            try {
                Map<String, String> quoteIdMap = new HashMap<>();
                if (getCustomerQuoteDTO(broker.getBrokerId()) != null) {
                    continue;
                }
                List<String> quoteTokens = tokenService.queryQuoteTokens(broker.getBrokerId());
                if (CollectionUtils.isEmpty(quoteTokens)) {
                    continue;
                }

                AdminUserReply adminUser = AdminUserReply.newBuilder()
                        .setUsername("sysinit@sys").setEmail("sysinit@sys")
                        .setAccountType(AccountType.ROOT_ACCOUNT)
                        .setOrgId(broker.getBrokerId())
                        .build();

                BrokerInfoDTO brokerInfoDTO = brokerClient.queryBrokerInfoById(broker.getBrokerId());
                if (brokerInfoDTO == null || StringUtils.isEmpty(brokerInfoDTO.getBrokerName())) {
                    continue;
                }
                CustomerQuoteDTO customerQuoteDTO = new CustomerQuoteDTO();
                List<CustomerQuoteDTO.Item> items = new ArrayList<>();
                for (String quoteToken : quoteTokens) {
                    CustomerQuoteDTO.Item item = new CustomerQuoteDTO.Item();
                    String id = sequenceGenerator.getLong().toString();
                    quoteIdMap.put(quoteToken, id);
                    item.setId(id);

                    List<CustomerQuoteDTO.ContentData> contentDataList = new ArrayList<>();
                    List<BrokerLanguageDTO> languages = brokerInfoDTO.getSupportLanguages();
                    for (BrokerLanguageDTO languageDTO : languages) {
                        CustomerQuoteDTO.ContentData contentData = new CustomerQuoteDTO.ContentData();
                        contentData.setEnable(true);
                        String language = languageDTO.getLanguage();
                        if (language.contains("-")) {
                            language = language.split("-")[0] + "_" + language.split("-")[1].toUpperCase();
                        }
                        contentData.setLocale(language);
                        contentData.setTabName(quoteToken);
                        contentDataList.add(contentData);
                    }
                    item.setContentlist(contentDataList);
                    items.add(item);
                }
                customerQuoteDTO.setItems(items);

                log.info("broker:{} customerQuoteDTO:{} admin:{}", broker.getBrokerId(), customerQuoteDTO, adminUser);
                editCustomerQuoteTokens(broker.getBrokerId(), customerQuoteDTO, adminUser);

                for (String quoteToken : quoteIdMap.keySet()) {
                    List<String> symbols = getQuoteSymbols(broker.getBrokerId(), quoteToken, 1);
                    if (CollectionUtils.isEmpty(symbols)) {
                        continue;
                    }
                    EditCustomerQuoteSymbolsPO po = new EditCustomerQuoteSymbolsPO();
                    po.setCustomerQuoteId(quoteIdMap.get(quoteToken));
                    po.setSymbols(symbols);
                    editCustomerQuoteSymbols(broker.getBrokerId(), po, adminUser);
                }
            } catch (Exception e) {
                log.warn("broker:{}", broker, e);
            }

        }

    }

    @Override
    public boolean editSymbolCustomLabel(long orgId, String symbolId, long labelId) {
        log.info("editSymbolCustomLabel:{},{},{}", orgId, symbolId, labelId);
        return symbolClient.editSymbolCustomLabel(orgId, symbolId, labelId);
    }

    @Override
    public boolean hideFromOpenapi(Long orgId, String symbolId, Boolean hideFromOpenapi) {
        log.info("hideFromOpenapi:{},{},{}", orgId, symbolId, hideFromOpenapi);
        return symbolClient.hideFromOpenapi(orgId, symbolId, hideFromOpenapi);
    }

    @Override
    public boolean forbidOpenapiTrade(Long orgId, String symbolId, Boolean forbidOpenapiTrade) {
        log.info("forbidOpenapiTrade:{},{},{}", orgId, symbolId, forbidOpenapiTrade);
        return symbolClient.forbidOpenapiTrade(orgId, symbolId, forbidOpenapiTrade);
    }

    @Override
    public ResultModel createSymbolTransfer(Long orgId, SymbolMatchTransferPO po) {
        SaveSymbolTransferRequest request = SaveSymbolTransferRequest.newBuilder()
                .setSourceBrokerId(orgId)
                .setMatchBrokerName(po.getMatchBrokerName())
                .setSymbolId(po.getSymbolId())
                .setEnable(1)
                .build();
        SaveSymbolTransferReply reply = symbolClient.saveSymbolTransfer(request);
        if (reply.getResult()) {
            return ResultModel.ok();
        }
        return ResultModel.error(reply.getMessage());
    }

    @Override
    public ResultModel closeSymbolTransfer(long orgId, String symbolId) {
        CloseSymbolTransferRequest request = CloseSymbolTransferRequest.newBuilder()
                .setBrokerId(orgId)
                .setSymbolId(symbolId)
                .build();
        SaveSymbolTransferReply reply = symbolClient.closeSymbolTransfer(request);
        if (reply.getResult()) {
            return ResultModel.ok();
        }
        return ResultModel.error(reply.getMessage());
    }

    @Override
    public AdminSimplyReply editSymbolExtraTags(long orgId, String symbolId, Map<String, Integer> tagMap) {
        return symbolClient.editSymbolExtraTags(orgId, symbolId, tagMap);
    }

    @Override
    public AdminSimplyReply editSymbolExtraConfigs(long orgId, String symbolId, Map<String, String> configMap) {
        return symbolClient.editSymbolExtraConfigs(orgId, symbolId, configMap);
    }
}
