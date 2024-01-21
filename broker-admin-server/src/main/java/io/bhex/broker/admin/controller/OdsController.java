package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.TokenHoldInfoDTO;
import io.bhex.broker.admin.controller.param.OdsQueryPo;
import io.bhex.broker.admin.controller.param.QueryBalanceTopPO;
import io.bhex.broker.admin.controller.param.QueryTopDataPO;
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.service.StatisticService;
import io.bhex.broker.admin.service.impl.OdsService;
import io.bhex.broker.grpc.admin.QuerySymbolReply;
import io.bhex.broker.grpc.statistics.QueryTopDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/v1/ods")
public class OdsController extends BrokerBaseController {

    @Autowired
    private OdsService odsService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private SymbolClient symbolClient;

    @RequestMapping(value = "/common/{type}", method = RequestMethod.POST)
    public ResultModel queryOdsData(@RequestBody @Valid OdsQueryPo po, @PathVariable String type) {
        Map<String, List<Map<String, Object>>> result = odsService.queryOdsData(getOrgId(), po, getRequestUser());
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/token/{type}", method = RequestMethod.POST)
    public ResultModel queryOdsTokenData(@RequestBody @Valid OdsQueryPo po, @PathVariable String type) {
        AdminUserReply adminUserReply = getRequestUser();
        Map<String, Map<String, List<Map<String, Object>>>> result = odsService.queryOdsTokenData(adminUserReply.getOrgId(), po, adminUserReply);
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/symbol/{type}", method = RequestMethod.POST)
    public ResultModel queryOdsSymbolData(@RequestBody @Valid OdsQueryPo po, @PathVariable String type) {
        Map<String, Map<String, List<Map<String, Object>>>> result = odsService.queryOdsSymbolData(getOrgId(), po, getRequestUser());
        return ResultModel.ok(result);
    }

    @RequestMapping(value = {"/token/balance_top", "/top_data/balance_top"}, method = RequestMethod.POST)
    public ResultModel queryBalanceTop(@RequestBody @Valid QueryBalanceTopPO po) {

        long userId = 0;
        if (StringUtils.isNotEmpty(po.getEmail()) || StringUtils.isNotEmpty(po.getPhone()) || po.getUserId() > 0) {
            Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
            if (combo2 != null) {
                userId = combo2.getV1();
            }
        }

        List<TokenHoldInfoDTO> list = statisticService.queryOrgBalanceTop(getOrgId(), po.getTokenId(), userId, po.getTop() > 500 ? 500 : po.getTop());

        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/top_data/{bizKey}", method = RequestMethod.POST)
    public ResultModel queryTopData(@RequestBody @Valid QueryTopDataPO po, @PathVariable String bizKey, AdminUserReply adminUser) {
        if (StringUtils.isNotEmpty(po.getDate())) {
            po.setStartDate(po.getDate());
            po.setEndDate(po.getDate());
        }

        QueryTopDataRequest.Builder builder = QueryTopDataRequest.newBuilder()
                .setOrgId(adminUser.getOrgId())
                .setBizKey(bizKey)
                .setStartDate(StringUtils.isEmpty(po.getStartDate()) ? "2018-01-01" : po.getStartDate())
                .setEndDate(StringUtils.isEmpty(po.getEndDate()) ? "2200-01-01" : po.getEndDate())
                .setLimit(po.getPageSize())
                .setLastIndex(po.getLastIndex())
                .setTradeType(po.getTradeType());

        if (StringUtils.isNotEmpty(po.getTokenId())) {
            builder.setToken(po.getTokenId());
        }

        if (StringUtils.isNotEmpty(po.getSymbolId())) {
            builder.setSymbol(po.getSymbolId());
        }

        List<Map<String, Object>> list = odsService.queryTopData(builder.build());

        return ResultModel.ok(list);
    }

    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/top_data/symbolQuoteTokens/{categroy}", method = RequestMethod.POST)
    public ResultModel querySymbolQuoteTokens(@PathVariable String categroy, AdminUserReply adminUser) {
        if (categroy == null || (!categroy.equals("spot") && !categroy.equals("contract"))) {
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }
        QuerySymbolReply reply = symbolClient.querySymbol(1, 500,
                categroy.equals("spot") ? 1 : 4, "", "",  adminUser.getOrgId(), null, null);

        List<String> tokens = reply.getSymbolDetailsList().stream()
                .map(s -> s.getQuoteTokenId()).distinct()
                .collect(Collectors.toList());

        return ResultModel.ok(tokens);
    }
}
