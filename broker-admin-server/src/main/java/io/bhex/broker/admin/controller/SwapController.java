package io.bhex.broker.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.token.TokenCategory;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.bhop.common.util.validation.ValidUtil;
import io.bhex.broker.admin.controller.dto.CustomLabelDTO;
import io.bhex.broker.admin.controller.dto.SymbolDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.service.BrokerTradeFeeSettingService;
import io.bhex.broker.admin.service.CustomLabelService;
import io.bhex.broker.admin.service.SymbolService;
import io.bhex.broker.grpc.admin.SymbolDetail;
import io.bhex.broker.grpc.admin.SymbolSwitchEnum;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 2019/9/18 3:58 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1/swap")
public class SwapController extends BaseController {

    private static final Integer FUTURE_CATEGORY = 4;

    @Autowired
    private SymbolService symbolService;

    @Autowired
    private BrokerTradeFeeSettingService brokerTradeFeeSettingService;

    @Autowired
    private CustomLabelService customLabelService;

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/query")
    public ResultModel query(@RequestBody @Valid QuerySymbolPO query, AdminUserReply adminUser) {
        long brokerId = adminUser.getOrgId();
        query.setCategory(FUTURE_CATEGORY);

        PaginationVO<SymbolDTO> vo = symbolService.querySymbol(query.getCurrent(), query.getPageSize(),
                query.getCategory(), null, query.getSymbolName(), brokerId, query.getExtraRequestInfos(), null);
        return ResultModel.ok(vo);
    }

    @RequestMapping(value = "/show")
    public ResultModel show() {
        return ResultModel.ok();
    }

    @BussinessLogAnnotation(opContent = "open contract symbol:{#po.symbolId}  trade")
    @RequestMapping(value = "/allow_trade")
    public ResultModel allowTrade(@RequestBody @Valid ChangeSymbolStatusPO po) {
        Long brokerId = getOrgId();
        Boolean isOk = symbolService.allowTrade(0L, po.getSymbolId(), Boolean.TRUE, brokerId);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "close contract symbol:{#po.symbolId}  trade")
    @RequestMapping(value = "/forbid_trade")
    public ResultModel forbidTrade(@RequestBody @Valid ChangeSymbolStatusPO po) {
        Long brokerId = getOrgId();
        Boolean isOk = symbolService.allowTrade(0L, po.getSymbolId(), Boolean.FALSE, brokerId);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "contract symbol:{#po.symbolId}  visible")
    @RequestMapping(value = "/allow_publish")
    public ResultModel allowPublish(@RequestBody @Valid ChangeSymbolStatusPO po, AdminUserReply adminUser) {
        long brokerId = adminUser.getOrgId();
        Boolean isOk = symbolService.publish(po.getSymbolId(), Boolean.TRUE, brokerId);
        if (isOk) {
            SymbolDetail symbolDetail = symbolService.queryBrokerSymbolById(brokerId, po.getSymbolId());
            brokerTradeFeeSettingService.addTradeFeeIfNotExisted(brokerId, symbolDetail.getExchangeId(), po.getSymbolId(), FUTURE_CATEGORY);
        }
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "contract symbol:{#po.symbolId}  unvisible")
    @RequestMapping(value = "/forbid_publish")
    public ResultModel forbidPublish(@RequestBody @Valid ChangeSymbolStatusPO po, AdminUserReply adminUser) {
        Long brokerId = adminUser.getOrgId();
        Boolean isOk = symbolService.publish(po.getSymbolId(), Boolean.FALSE, brokerId);
        return ResultModel.ok(isOk);
    }


    @RequestMapping(value = "/symbol_switch", method = RequestMethod.POST)
    public ResultModel symbolSwitch(@RequestBody @Valid SymbolSwitchPO po) {
        Map<Integer, String> opMap = new HashMap<>();
        opMap.put(SymbolSwitchEnum.SYMBOL_SHOW_SWITCH_VALUE, " symbol:{symbol} show switch");

        if (!opMap.containsKey(po.getSwitchType())) {
            return ResultModel.error("error param");
        }

        boolean r = symbolService.editSymbolSwitch(getOrgId(), po, null);
        String opContent = (po.getOpen() ? "open" : "close") + opMap.get(po.getSwitchType()).replace("{symbol}", po.getSymbolId());
        saveBizLog("symbolSwitch", opContent, JSON.toJSONString(po), r ? 0 : 1);

        return ResultModel.ok(r);
    }

    @RequestMapping(value = "/query_quote_symbols", method = RequestMethod.POST)
    public ResultModel queryQuoteSymbols(@RequestBody @Valid EditQuoteSymbolsPO po) {
        List<String> symbols = symbolService.getQuoteSymbols(getOrgId(), null, TokenCategory.FUTURE_CATEGORY_VALUE);
        return ResultModel.ok(symbols);
    }

    @RequestMapping(value = "/edit_quote_symbols", method = RequestMethod.POST)
    public ResultModel editQuoteSymbols(@RequestBody @Valid EditQuoteSymbolsPO po) {
        if (CollectionUtils.isEmpty(po.getSymbols())) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        boolean r = symbolService.editQuoteSymbols(getOrgId(), null, po.getSymbols(), TokenCategory.FUTURE_CATEGORY_VALUE);
        return ResultModel.ok(r);
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

}
