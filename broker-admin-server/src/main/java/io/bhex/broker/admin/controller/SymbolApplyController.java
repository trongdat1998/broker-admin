package io.bhex.broker.admin.controller;


import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.bhadmin.ContractApplyObj;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.ContractApplyDTO;
import io.bhex.broker.admin.controller.dto.SymbolApplyRecordDTO;
import io.bhex.broker.admin.controller.param.ContractApplyPO;
import io.bhex.broker.admin.controller.param.ListApplyRecordPO;
import io.bhex.broker.admin.controller.param.SymbolApplyPO;
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.grpc.client.impl.ContractSymbolClient;
import io.bhex.broker.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static io.netty.util.internal.StringUtil.COMMA;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SymbolApplyController {
    @Autowired
    private SymbolClient symbolClient;
    @Autowired
    private ContractSymbolClient contractSymbolClient;

    @PostMapping("/symbol/apply_spot_symbol")
    public ResultModel applySpotSymbol(@RequestBody @Validated SymbolApplyPO applyPO, AdminUserReply adminUser) {
        Long brokerId = adminUser.getOrgId();
        applyPO.setBaseToken(applyPO.getBaseToken().trim());
        applyPO.setQuoteToken(applyPO.getQuoteToken().trim());
        applyPO.setSymbolId(applyPO.getBaseToken().concat(applyPO.getQuoteToken()));
        if (applyPO.getBaseToken().equalsIgnoreCase(applyPO.getQuoteToken())) {
            throw new BizException(ErrorCode.BE_DIFFERENT_WITH_QUOTE_AND_BASE);
        }
        applyPO.setSymbolId(applyPO.getBaseToken().concat(applyPO.getQuoteToken()));
        if (StringUtils.isEmpty(applyPO.getMergeDigitDepth())) {
            String[] digitMergedItem = new String[3];
            for (int i = 1; i <= digitMergedItem.length; ++i) {
                digitMergedItem[digitMergedItem.length - i] = applyPO.getMinPricePrecision()
                        .multiply(BigDecimal.TEN.pow(i - 1))
                        .stripTrailingZeros()
                        .toPlainString();
            }
            applyPO.setMergeDigitDepth(StringUtils.join(digitMergedItem, COMMA));
        }
        return ResultModel.ok(symbolClient.applySymbol(applyPO.toApplyObj(brokerId, brokerId)));
    }

    @PostMapping("/symbol/spot_symbol_apply_list")
    public ResultModel<PaginationVO<SymbolApplyRecordDTO>> listSymbolApplyRecords(@RequestBody @Validated ListApplyRecordPO po, AdminUserReply adminUser) {
        Long brokerId = adminUser.getOrgId();
        Integer current = Objects.isNull(po.getCurrent()) ? 1 : po.getCurrent();
        Integer pageSize = Objects.isNull(po.getPageSize()) ? 30 : po.getPageSize();
        PaginationVO<SymbolApplyRecordDTO> resultPagination = symbolClient.listSymbolRecordList(brokerId, current, pageSize);
        return ResultModel.ok(resultPagination);
    }

    @PostMapping("/swap/apply_contract_symbol")
    public ResultModel applyContract(@RequestBody @Validated ContractApplyPO applyPO, AdminUserReply adminUser) {
        String symbolId;
        if (Objects.isNull(applyPO.getId())) {
            symbolId = contractSymbolClient.nextFuturesSymbolId(applyPO.getDisplayUnderlyingId());
        } else {
            symbolId = applyPO.getSymbolId();
        }

        applyPO.setState(0);
        if (applyPO.getDisplayUnderlyingId().equalsIgnoreCase(applyPO.getCurrency())) {
            throw new BizException(ErrorCode.BE_DIFFERENT_WITH_QUOTE_AND_BASE);
        }
        if (StringUtils.isEmpty(applyPO.getDigitMergeList())) {
            String[] digitMergedItem = new String[3];
            for (int i = 1; i <= digitMergedItem.length; ++i) {
                digitMergedItem[digitMergedItem.length - i] = applyPO.getMinPricePrecision()
                        .multiply(BigDecimal.TEN.pow(i - 1))
                        .stripTrailingZeros()
                        .toPlainString();
            }
            applyPO.setDigitMergeList(StringUtils.join(digitMergedItem, COMMA));
        }
        String symbolNameLocaleJson = processLocaleName(applyPO.getSymbolNameLocaleList());
        String riskLimitJson = processRiskLimit(applyPO.getRiskLimitList());
        ContractApplyObj symbolFuturesRecord = applyPO.toFuturesRecord(adminUser.getOrgId(), symbolId, symbolNameLocaleJson, riskLimitJson);
        return ResultModel.ok(contractSymbolClient.saveSymbolRecord(symbolFuturesRecord));
    }

    private String processLocaleName(List<ContractApplyPO.SymbolNameLocale> symbolNameLocaleList) {
        if (CollectionUtils.isEmpty(symbolNameLocaleList)) {
            return StringUtils.EMPTY;
        }
        for (ContractApplyPO.SymbolNameLocale s : symbolNameLocaleList) {
            if (StringUtils.isEmpty(s.getLocale()) || StringUtils.isEmpty(s.getLocale())) {
                throw new BizException(ErrorCode.SYMBOL_NAME_LOCALE_REQUIRED);
            }
        }
        return JsonUtil.defaultGson().toJson(symbolNameLocaleList);
    }

    private String processRiskLimit(List<ContractApplyPO.RiskLimit> riskLimitList) {
        if (CollectionUtils.isEmpty(riskLimitList)) {
            return StringUtils.EMPTY;
        }
        for (ContractApplyPO.RiskLimit s : riskLimitList) {
            if (Objects.isNull(s.getInitialMargin()) || Objects.isNull(s.getMaintainMargin()) || Objects.isNull(s.getRiskLimitAmount())) {
                throw new BizException(ErrorCode.SYMBOL_RISK_LIMIT_REQUIRED);
            }
        }
        return JsonUtil.defaultGson().toJson(riskLimitList);
    }


    @AccessAnnotation(verifyAuth = false)
    @PostMapping("/swap/contract_symbol_apply_list")
    public ResultModel<PaginationVO<ContractApplyDTO>> listContractApplyRecords(@RequestBody @Validated ListApplyRecordPO po, AdminUserReply adminUser) {
        Integer current = Objects.isNull(po.getCurrent()) ? 1 : po.getCurrent();
        Integer pageSize = Objects.isNull(po.getPageSize()) ? 30 : po.getPageSize();
        PaginationVO<ContractApplyDTO> resultPagination = contractSymbolClient.listSymbolRecordList(adminUser.getOrgId(), current, pageSize);
        return ResultModel.ok(resultPagination);
    }
}
