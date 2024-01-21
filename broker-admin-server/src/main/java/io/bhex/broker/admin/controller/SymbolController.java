package io.bhex.broker.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.token.QueryBrokerExchangeSymbolsReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.bhop.common.util.validation.ValidUtil;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.OrderClient;
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.service.*;
import io.bhex.broker.admin.service.impl.BrokerTaskConfigService;
import io.bhex.broker.grpc.admin.SaveBrokerTaskConfigReply;
import io.bhex.broker.grpc.admin.SymbolAgencyReply;
import io.bhex.broker.grpc.admin.SymbolDetail;
import io.bhex.broker.grpc.admin.SymbolSwitchEnum;
import io.bhex.broker.grpc.common.AdminSimplyReply;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 9:15 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/symbol")
public class SymbolController extends BrokerBaseController {

    private static final Integer COIN_CATEGORY = 1;

    @Autowired
    private SymbolService symbolService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private BrokerTradeFeeSettingService brokerTradeFeeSettingService;
    @Autowired
    private BrokerTaskConfigService brokerTaskConfigService;

    @Autowired
    private CustomLabelService customLabelService;

    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;

    private boolean symbolEnable(Long brokerId, String symbolId) {
        SymbolDetail detail = symbolService.queryBrokerSymbolById(brokerId, symbolId);
        return detail != null && detail.getPublished();
    }

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ResultModel querySymbol(@RequestBody @Valid QuerySymbolPO query, AdminUserReply adminUser) {
        if (!StringUtils.isEmpty(query.getSymbolName()) && !ValidUtil.isSymbol(query.getSymbolName().toUpperCase())) {
            PaginationVO<SymbolDTO> r = new PaginationVO<>();
            r.setList(Lists.newArrayList());
            r.setCurrent(1);
            r.setPageSize(query.getPageSize());
            r.setTotal(0);
            return ResultModel.ok(r);
        }
        PaginationVO<SymbolDTO> vo = symbolService.querySymbol(query.getCurrent(), query.getPageSize(),
                query.getCategory(), query.getQuoteToken(), Strings.nullToEmpty(query.getSymbolName()).toUpperCase(), adminUser.getOrgId(),
                query.getExtraRequestInfos(), query.getCustomerQuoteId());
        return ResultModel.ok(vo);
    }


    @BussinessLogAnnotation(opContent = "symbol:{#po.symbolId} visible")
    @RequestMapping(value = "/allow_publish", method = RequestMethod.POST)
    public ResultModel allowPublish(@RequestBody @Valid ChangeSymbolStatusPO po, AdminUserReply adminUser) {
        long brokerId = adminUser.getOrgId();

        if (!symbolService.availableInExchange(brokerId, po.getSymbolId())) {
            return ResultModel.error("exchange.symbol.unvailable");
        }

//        int pageSize = 1000;
//        for (int current = 1; current < 100; current++) {
//            QueryBrokerExchangeSymbolsReply reply = symbolService
//                    .queryBhExchangeSymbolsByBrokerId(po.getExchangeId(), brokerId, current, pageSize, COIN_CATEGORY);
//            if (CollectionUtils.isEmpty(reply.getExchangeSymbolDetailsList())) {
//                return ResultModel.error("exchange.symbol.unvailable");
//            }
//            boolean available = reply.getExchangeSymbolDetailsList().stream().anyMatch(s -> s.getPublished() && s.getSymbolId().equals(po.getSymbolId()));
//            if (available) {
//                break;
//            }
//            if (reply.getExchangeSymbolDetailsList().size() < pageSize) {
//                return ResultModel.error("exchange.symbol.unvailable");
//            }
//        }

        SymbolDetail symbolDetail = symbolService.queryBrokerSymbolById( brokerId, po.getSymbolId());
        if (po.getCategory() == 1) {
            String notPublishedToken = "";

            PaginationVO<TokenDTO> vo = tokenService.queryTokenById(1, 1, 1,
                    symbolDetail.getBaseTokenId(), brokerId, null);
            if (vo.getTotal() == 0) {
                return ResultModel.error("token:" + symbolDetail.getBaseTokenId() + " not existed");
            }
            if (!vo.getList().get(0).getIsPublished()) {
                notPublishedToken = symbolDetail.getBaseTokenId();
                log.info("brokerId:{} notPublishedToken:{}", brokerId, notPublishedToken);
                if (po.getPublishToken()) {
                    Boolean isOk = tokenService.publish(symbolDetail.getBaseTokenId(), Boolean.TRUE, brokerId);
                    log.info("brokerId:{} publish token:{} isOk:{}", brokerId, symbolDetail.getBaseTokenId(), isOk);
                    if (!isOk) {
                        return ResultModel.error(ErrorCode.TOKEN_MISSING.getCode(), ErrorCode.TOKEN_MISSING.getDesc(), "");
                    }
                }
            }
            vo = tokenService.queryTokenById(1, 1, 1,
                    symbolDetail.getQuoteTokenId(), brokerId, null);
            if (vo.getTotal() == 0){
                return ResultModel.error("token:" + symbolDetail.getQuoteTokenId() + " not existed");
            }

            if (!vo.getList().get(0).getIsPublished()) {
                notPublishedToken = notPublishedToken + " " + symbolDetail.getQuoteTokenId();
                log.info("brokerId:{} notPublishedToken:{}", brokerId, notPublishedToken);
                if (po.getPublishToken()) {
                    Boolean isOk = tokenService.publish(symbolDetail.getQuoteTokenId(), Boolean.TRUE, brokerId);
                    log.info("brokerId:{} publish token:{} isOk:{}", brokerId, symbolDetail.getQuoteTokenId(), isOk);
                    if (!isOk) {
                        return ResultModel.error(ErrorCode.TOKEN_MISSING.getCode(), ErrorCode.TOKEN_MISSING.getDesc(), "");
                    }
                }
            }
            if (!notPublishedToken.equals("") && !po.getPublishToken()) {
                return ResultModel.error(ErrorCode.SYMBOL_TOKEN_NOT_PUBLISHED.getCode(), notPublishedToken, notPublishedToken);
            }

            addTradeZoneSymbol(brokerId, symbolDetail.getBaseTokenId(), symbolDetail.getQuoteTokenId(), adminUser);

        }

        if (opFrequently(po.getSymbolId(), 10)) {
            return ResultModel.error("op.frequently");
        }

        Boolean isOk = symbolService.publish(po.getSymbolId(), Boolean.TRUE, brokerId);
        log.info("publish ok:{}", isOk);
        brokerTradeFeeSettingService.addTradeFeeIfNotExisted(brokerId, symbolDetail.getExchangeId(), po.getSymbolId(), COIN_CATEGORY);
        return ResultModel.ok();
    }

    /**
     * 判断新增币对 是否增加到交易区了
     *
     * @param brokerId
     * @param baseTokenId
     * @param quoteTokenId
     * @param adminUser
     * @return
     */
    private boolean addTradeZoneSymbol(long brokerId, String baseTokenId, String quoteTokenId, AdminUserReply adminUser) {
        CustomerQuoteDTO dto = symbolService.queryCustomerQuoteTokens(brokerId);
        List<CustomerQuoteDTO.Item> items = dto.getItems();
        if (!CollectionUtils.isEmpty(items)) {
            String customerQuoteId = null;
            List<String> quoteTokens = tokenService.queryQuoteTokens(brokerId);
            for (CustomerQuoteDTO.Item item : items) {
                List<CustomerQuoteDTO.ContentData> dataList = Lists.newArrayList(item.getContentlist());
                for (CustomerQuoteDTO.ContentData data : dataList) {
                    if (!quoteTokens.contains(data.getTabName())) {
                        break;
                    }
                    if (data.getTabName().equals(quoteTokenId)) {
                        customerQuoteId = item.getId();
                    }
                }
            }

            if (customerQuoteId != null) {
                List<String> quoteSymbols = symbolService.getCustomerQuoteSymbols(brokerId, customerQuoteId);
                if (!quoteSymbols.contains(baseTokenId + "/" + quoteTokenId)) {
                    quoteSymbols.add(baseTokenId + "/" + quoteTokenId);
                    EditCustomerQuoteSymbolsPO editCustomerQuoteSymbolsPO = new EditCustomerQuoteSymbolsPO();
                    editCustomerQuoteSymbolsPO.setCustomerQuoteId(customerQuoteId);
                    editCustomerQuoteSymbolsPO.setSymbols(quoteSymbols);
                    symbolService.editCustomerQuoteSymbols(brokerId, editCustomerQuoteSymbolsPO, adminUser);
                }
                return true;
            }
        }
        return false;
    }

    @BussinessLogAnnotation(opContent = "symbol:{#po.symbolId} unvisible")
    @RequestMapping(value = "/forbid_publish", method = RequestMethod.POST)
    public ResultModel forbidPublish(@RequestBody @Valid ChangeSymbolStatusPO po, AdminUserReply adminUser) {
        if (opFrequently(po.getSymbolId(), 10)) {
            return ResultModel.error("op.frequently");
        }

//        if (po.getCategory() == 1) {
//            CancelMatchOrderReply reply = orderClient.cancelBrokerOrders(brokerId, po.getExchangeId(), po.getSymbolId());
//            if (reply.getCancelMatchOrderReplyCode() == CancelMatchOrderReply.CancelMatchOrderReplyCode.HIGH_FREQ) {
//                return ResultModel.error("op.frequently");
//            }
//            if (reply.getCancelMatchOrderReplyCode() != CancelMatchOrderReply.CancelMatchOrderReplyCode.OK) {
//                log.error("cancelBrokerOrders error org:{} symbol:{} code:{}", brokerId, po.getSymbolId(), reply.getCancelMatchOrderReplyCode());
//                return ResultModel.error("error");
//            }
//        }

        //TODO 调用saas 交易所下撮合、取消所有转发到我的币对转发关系

        Boolean isOk = symbolService.publish(po.getSymbolId(), Boolean.FALSE, adminUser.getOrgId());

        return ResultModel.ok(isOk);
    }

//    @RequestMapping(value = "/agency", method = RequestMethod.POST)
//    public ResultModel agencySymbol(@RequestBody @Valid AgencySymbolPO po) {
//        AdminUserReply requestUser = getRequestUser();
//        long brokerId = requestUser.getOrgId();
//        SymbolAgencyReply reply = symbolService.agencySymbol(po.getExchangeId(), po.getSymbolIds(), brokerId);
//        if (!reply.getResult()) {
//            return ResultModel.error(reply.getMessage());
//        }
//        for (String symbolId : po.getSymbolIds()) {
//            brokerTradeFeeSettingService.addTradeFeeIfNotExisted(brokerId, po.getExchangeId(), symbolId, COIN_CATEGORY);
//        }
//        return ResultModel.ok();
//    }

//    @RequestMapping(value = "/exchange_symbols", method = RequestMethod.POST)
//    public ResultModel querySymbolByExchange(@RequestBody @Valid QuerySymbolByExchangePO po) {
//        long brokerId = getOrgId();
//        Integer category = 0;
//        if (Objects.nonNull(po.getCategory()) && po.getCategory() != 0) {
//            category = po.getCategory();
//        }
//        PaginationVO<QuerySymbolsByExchangeDTO> reply = symbolService.queryExchangeSymbolsByBrokerId(po.getExchangeId(), brokerId, po.getCurrent(), po.getPageSize(), category);
//        return ResultModel.ok(reply);
//    }

    @RequestMapping(value = "/symbol_switch", method = RequestMethod.POST)
    public ResultModel symbolSwitch(@RequestBody @Valid SymbolSwitchPO po, AdminUserReply adminUser) {


        Map<Integer, String> opMap = new HashMap<>();
        opMap.put(SymbolSwitchEnum.SYMBOL_BAN_SELL_SWITCH_VALUE, " symbol:{symbol} ban sell switch");
        opMap.put(SymbolSwitchEnum.SYMBOL_BAN_BUY_SWITCH_VALUE, " symbol:{symbol} ban buy switch");
        opMap.put(SymbolSwitchEnum.SYMBOL_SHOW_SWITCH_VALUE, " symbol:{symbol} show switch");
        opMap.put(SymbolSwitchEnum.SYMBOL_FILTER_TOP_SWITCH_VALUE, " symbol:{symbol} filter top switch");
        opMap.put(SymbolSwitchEnum.SYMBOL_PLAN_SWITCH_VALUE, " symbol:{symbol} allow plan switch");

        if (!opMap.containsKey(po.getSwitchType())) {
            return ResultModel.error("error param");
        }

        if (!symbolEnable(adminUser.getOrgId(), po.getSymbolId())) {
            return ResultModel.error("symbol.disabled");
        }


        boolean r = symbolService.editSymbolSwitch(getOrgId(), po, null);
        String opContent = (po.getOpen() ? "open" : "close") + opMap.get(po.getSwitchType()).replace("{symbol}", po.getSymbolId());
        saveBizLog("symbolSwitch", opContent, JSON.toJSONString(po), r ? 0 : 1);

        return ResultModel.ok(r);
    }


    @RequestMapping(value = "/task/query_configs", method = RequestMethod.POST)
    public ResultModel<String> querySymbolTaskConfigs(@RequestBody @Valid QuerySymbolTasksPO po) {
        return ResultModel.ok(brokerTaskConfigService.getSymbolTaskConfigs(getOrgId(), po.getPageSize(), po.getFromId()));
    }

    @RequestMapping(value = "/task/cancel_config", method = RequestMethod.POST)
    public ResultModel<String> cancelSymbolTaskConfig(@RequestBody @Valid IdPO po) {
        BrokerSymbolTaskConfigDTO dto = new BrokerSymbolTaskConfigDTO();
        dto.setStatus(0);
        dto.setId(po.getId());
        AdminUserReply adminUserReply = getRequestUser();
        SaveBrokerTaskConfigReply reply = brokerTaskConfigService.editSymbolTaskConfig(adminUserReply.getOrgId(), adminUserReply, dto);
        if (!reply.getResult()) {
            return ResultModel.error(reply.getMessage());
        }
        return ResultModel.ok();
    }


    private boolean hasBeenInTradeZone(long brokerId, String baseTokenId, String quoteTokenId, AdminUserReply adminUser) {
        CustomerQuoteDTO dto = symbolService.queryCustomerQuoteTokens(brokerId);
        List<CustomerQuoteDTO.Item> items = dto.getItems();
        if (CollectionUtils.isEmpty(items)) {
            return false;
        }
        for (CustomerQuoteDTO.Item item : items) {
            List<String> quoteSymbols = symbolService.getCustomerQuoteSymbols(brokerId, item.getId());
            if (!CollectionUtils.isEmpty(quoteSymbols) && quoteSymbols.contains(baseTokenId + "/" + quoteTokenId)) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value = "/task/edit_config", method = RequestMethod.POST)
    public ResultModel<String> editSymbolTaskConfig(@RequestBody @Valid BrokerSymbolTaskConfigDTO po, AdminUserReply adminUser) {
        if (po.getStatus() == 1 && po.getActionTime() < System.currentTimeMillis() + 60_000) {
            return ResultModel.error("request.parameter.error");
        }
        AdminUserReply adminUserReply = getRequestUser();
        long brokerId = adminUserReply.getOrgId();
        if (!symbolService.availableInExchange(brokerId, po.getSymbolId())) {
            return ResultModel.error("exchange.symbol.unvailable");
        }

        String notPublishedToken = "";
        SymbolDetail detail = symbolService.queryBrokerSymbolById(brokerId, po.getSymbolId());
        boolean suc = addTradeZoneSymbol(brokerId, detail.getBaseTokenId(), detail.getQuoteTokenId(), adminUser);
        if (!suc) {
            if (!hasBeenInTradeZone(brokerId, detail.getBaseTokenId(), detail.getQuoteTokenId(), adminUser)) {
                return ResultModel.error("symbol.notin.tradezone");
            }
        }

        PaginationVO<TokenDTO> vo = tokenService.queryTokenById(1, 1, 1,
                detail.getBaseTokenId(), brokerId, null);
        if (vo.getTotal() == 0 || !vo.getList().get(0).getIsPublished()) {
            notPublishedToken = detail.getBaseTokenId();
            if (po.getPublishToken()) {
                Boolean isOk = tokenService.publish(detail.getBaseTokenId(), Boolean.TRUE, brokerId);
                log.info("brokerId:{} publish token:{} isOk:{}", brokerId, detail.getBaseTokenId(), isOk);
                if (!isOk) {
                    return ResultModel.error(ErrorCode.TOKEN_MISSING.getCode(), ErrorCode.TOKEN_MISSING.getDesc(), "");
                }
            }
        }
        vo = tokenService.queryTokenById(1, 1, 1,
                detail.getQuoteTokenId(), brokerId, null);
        if (vo.getTotal() == 0 || !vo.getList().get(0).getIsPublished()) {
            notPublishedToken = notPublishedToken + " " + detail.getQuoteTokenId();
            if (po.getPublishToken()) {
                Boolean isOk = tokenService.publish(detail.getQuoteTokenId(), Boolean.TRUE, brokerId);
                log.info("brokerId:{} publish token:{} isOk:{}", brokerId, detail.getQuoteTokenId(), isOk);
                if (!isOk) {
                    return ResultModel.error(ErrorCode.TOKEN_MISSING.getCode(), ErrorCode.TOKEN_MISSING.getDesc(), "");
                }
            }
        }

        if (!notPublishedToken.equals("")) {
            log.warn("notPublishedToken:{}", notPublishedToken);
            //return ResultModel.error(ErrorCode.SYMBOL_TOKEN_NOT_PUBLISHED.getCode(), "", notPublishedToken);
        }


        SaveBrokerTaskConfigReply reply = brokerTaskConfigService.editSymbolTaskConfig(brokerId, adminUserReply, po);
        if (!reply.getResult()) {
            return ResultModel.error(reply.getMessage());
        }
        return ResultModel.ok();
    }


    @RequestMapping(value = "/edit_quote_tokens", method = RequestMethod.POST)
    public ResultModel editQuoteTokens(@RequestBody @Valid EditQuoteTokensPO po) {
        if (CollectionUtils.isEmpty(po.getTokens())) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        boolean r = symbolService.editQuoteTokens(getOrgId(), po.getTokens());
        return ResultModel.ok(r);
    }

    @RequestMapping(value = "/query_quote_symbols", method = RequestMethod.POST)
    public ResultModel queryQuoteSymbols(@RequestBody @Valid EditQuoteSymbolsPO po) {
        List<String> symbols = symbolService.getQuoteSymbols(getOrgId(), po.getTokenId(), 1);
        return ResultModel.ok(symbols);
    }

    @RequestMapping(value = "/edit_quote_symbols", method = RequestMethod.POST)
    public ResultModel editQuoteSymbols(@RequestBody @Valid EditQuoteSymbolsPO po) {
        if (CollectionUtils.isEmpty(po.getSymbols())) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        if (StringUtils.isEmpty(po.getTokenId())) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        boolean r = symbolService.editQuoteSymbols(getOrgId(), po.getTokenId(), po.getSymbols(), 1);
        return ResultModel.ok(r);
    }

    @RequestMapping(value = "/edit_symbol_filter_time", method = RequestMethod.POST)
    public ResultModel editSymbolFilterTime(@RequestBody @Valid EditSymbolFilterTimePO po) {
        if (StringUtils.isEmpty(po.getSymbol()) || po.getFilterTime() == null) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        boolean r = symbolService.editSymbolFilterTime(getOrgId(), po.getSymbol(), po.getFilterTime());
        return ResultModel.ok(r);
    }

    @RequestMapping(value = "/edit_cumstomer_quote_tokens", method = RequestMethod.POST)
    public ResultModel editCustomerQuoteTokens(@RequestBody @Valid CustomerQuoteDTO po, AdminUserReply adminUser) {
        if(CollectionUtils.isEmpty(po.getItems())) {
            return ResultModel.error("empty.items");
        }
        symbolService.editCustomerQuoteTokens(adminUser.getOrgId(), po, adminUser);
        return ResultModel.ok();
    }

    @RequestMapping(value = "/query_cumstomer_quote_tokens", method = RequestMethod.POST)
    public ResultModel queryCustomerQuoteTokens(AdminUserReply adminUser) {
        CustomerQuoteDTO dto = symbolService.queryCustomerQuoteTokens(adminUser.getOrgId());
        return ResultModel.ok(dto != null ? dto.getItems() : new ArrayList<>());
    }

    @RequestMapping(value = "/query_cumstomer_quote_symbols", method = RequestMethod.POST)
    public ResultModel queryCustomerQuoteSymbols(@RequestBody @Valid EditCustomerQuoteSymbolsPO po) {
        if (po.getCustomerQuoteId() == null || po.getCustomerQuoteId().equals("")) {
            List<String> symbols = symbolService.getQuoteSymbols(getOrgId(), "", 1);
            return ResultModel.ok(symbols);
        }
        return ResultModel.ok(symbolService.getCustomerQuoteSymbols(getOrgId(), po.getCustomerQuoteId()));
    }

    @RequestMapping(value = "/edit_cumstomer_quote_symbols", method = RequestMethod.POST)
    public ResultModel editCustomerQuoteSymbols(@RequestBody @Valid EditCustomerQuoteSymbolsPO po, AdminUserReply adminUser) {
        if (CollectionUtils.isEmpty(po.getSymbols())) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        if (StringUtils.isEmpty(po.getCustomerQuoteId())) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        symbolService.editCustomerQuoteSymbols(adminUser.getOrgId(), po, adminUser);
        return ResultModel.ok();
    }

    @RequestMapping(value = "/edit_symbol_custom_label", method = RequestMethod.POST)
    public ResultModel editSymbolFilterTime(@RequestBody Map<String, Object> paramMap) {
        String symbolId = paramMap.getOrDefault("symbolId", "").toString();
        if (Strings.isNullOrEmpty(symbolId)) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        Long labelId = Long.valueOf(paramMap.getOrDefault("labelId", "0").toString());
        boolean r = symbolService.editSymbolCustomLabel(getOrgId(), symbolId, labelId);
        return ResultModel.ok(r);
    }

    @RequestMapping(value = "/query_custom_labels")
    public ResultModel queryCustomLabel(@RequestBody @Valid QueryCustomLabelPO po) {
        po.setOrgId(getOrgId());
        po.setType(2);
        List<CustomLabelDTO> labelDTOList = customLabelService.queryCustomLabel(po);
        return ResultModel.ok(labelDTOList);
    }

    @RequestMapping(value = "/hide_from_openapi", method = RequestMethod.POST)
    public ResultModel hideFromOpenapi(@RequestBody Map<String, Object> paramMap) {
        String symbolId = paramMap.getOrDefault("symbolId", "").toString();
        if (Strings.isNullOrEmpty(symbolId)) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        Boolean hideFromOpenapi = Boolean.valueOf(paramMap.getOrDefault("hideFromOpenapi", "false").toString());
        boolean r = symbolService.hideFromOpenapi(getOrgId(), symbolId, hideFromOpenapi);
        return ResultModel.ok(r);
    }

    @RequestMapping(value = "/forbid_openapi_trade", method = RequestMethod.POST)
    public ResultModel forbidOpenapiTrade(@RequestBody Map<String, Object> paramMap) {
        String symbolId = paramMap.getOrDefault("symbolId", "").toString();
        if (Strings.isNullOrEmpty(symbolId)) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        Boolean forbidOpenapiTrade = Boolean.valueOf(paramMap.getOrDefault("forbidOpenapiTrade", "false").toString());
        boolean r = symbolService.forbidOpenapiTrade(getOrgId(), symbolId, forbidOpenapiTrade);
        return ResultModel.ok(r);
    }

    @RequestMapping(value = "/edit_symbol_tag", method = RequestMethod.POST)
    public ResultModel editSymbolExtraTag(@RequestBody @Valid EditSymbolExtraTagPO po) {
        AdminSimplyReply r = symbolService.editSymbolExtraTags(getOrgId(), po.getSymbolId(), po.getTags());
        if (r.getResult()) {
            return ResultModel.ok();
        } else {
            return ResultModel.error(r.getMessage());
        }
    }

    @RequestMapping(value = "/edit_symbol_config", method = RequestMethod.POST)
    public ResultModel editSymbolExtraConfig(@RequestBody @Valid EditSymbolExtraConfigPO po) {
        AdminSimplyReply r = symbolService.editSymbolExtraConfigs(getOrgId(), po.getSymbolId(), po.getConfigs());
        if (r.getResult()) {
            return ResultModel.ok();
        } else {
            return ResultModel.error(r.getMessage());
        }
    }

    @RequestMapping(value = "/create_symbol_transfer")
    public ResultModel createSymbolTransfer(@RequestBody @Valid SymbolMatchTransferPO po, AdminUserReply adminUser) {
        ResultModel resultModel = symbolService.createSymbolTransfer(adminUser.getOrgId(), po);
        return resultModel;
    }

    @RequestMapping(value = "/close_symbol_transfer")
    public ResultModel closeSymbolTransfer(@RequestBody Map<String, Object> paramMap, AdminUserReply adminUser) {
        String symbolId = MapUtils.getString(paramMap,"symbolId", "");
        if (Strings.isNullOrEmpty(symbolId) || !ValidUtil.isSymbol(symbolId)) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        ResultModel resultModel = symbolService.closeSymbolTransfer(adminUser.getOrgId(), symbolId);
        return resultModel;
    }
    @RequestMapping(value = "/edit_api_config", method = RequestMethod.POST)
    public ResultModel editApiSymbolConfig(@RequestBody @Valid BaseConfigPO po, AdminUserReply adminUserReply) {
        po.setWithLanguage(false);
        baseConfigService.editConfig(adminUserReply.getOrgId(), po, adminUserReply);

        return ResultModel.ok();
    }

    @RequestMapping(value = "/get_api_group_configs", method = RequestMethod.POST)
    public ResultModel getGroupConfigs(@RequestBody @Valid BaseConfigPO po) {
        po.setWithLanguage(false);
        List<BaseConfigDTO> list = baseConfigService.getConfigsByGroup(getOrgId(), po);
        return ResultModel.ok(list);
    }
}
