package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.OrderDetailDTO;
import io.bhex.broker.admin.controller.dto.TradeDetailDTO;
import io.bhex.broker.admin.controller.param.SimpleOrderPO;
import io.bhex.broker.admin.grpc.client.OrderClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-07-20 10:19
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/margin/transaction_order")
public class MarginOrderController extends BrokerBaseController {
    @Autowired
    private OrderClient orderClient;

    @RequestMapping(value = "/current_orders", method = RequestMethod.POST)
    public ResultModel<List<OrderDetailDTO>> getNewOrders(HttpServletRequest request, @RequestBody @Valid SimpleOrderPO po) {
        if ((po.getUserId() == null || po.getUserId() == 0)
                && StringUtils.isEmpty(po.getPhone()) && StringUtils.isEmpty(po.getEmail())) {
            return ResultModel.error("userId is null");
        }
        Combo2<Long, Long> combo2 = getUserIdAndMarginAccountId(po, getOrgId());
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long accountId = combo2.getV2();
        Long userId = combo2.getV1();
        String symbolId = StringUtils.isNotEmpty(po.getSymbolId()) ? po.getSymbolId() : StringUtils.isAnyEmpty(po.getQuoteTokenId(), po.getBaseTokenId())
                ? null
                : po.getBaseTokenId() + po.getQuoteTokenId();

        Long lastOrderId = po.getLastId() == null || po.getLastId() < 0 ? 0 : po.getLastId();
        List<OrderDetailDTO> list = orderClient.getNewOrders(getOrgId(), accountId, symbolId, lastOrderId, po.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }

        for (OrderDetailDTO orderDetailDTO : list) {
            orderDetailDTO.setUserId(userId);
        }

        return ResultModel.ok(list);
    }
    @RequestMapping(value = "/history_orders", method = RequestMethod.POST)
    public ResultModel<List<OrderDetailDTO>> getHistoryOrders(@RequestBody @Valid SimpleOrderPO po) {
        if ((po.getUserId() == null || po.getUserId() == 0)
                && StringUtils.isEmpty(po.getPhone()) && StringUtils.isEmpty(po.getEmail())) {
            return ResultModel.error("userId is null");
        }
        Combo2<Long, Long> combo2 = getUserIdAndMarginAccountId(po, getOrgId());
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long accountId = combo2.getV2();
        Long userId = combo2.getV1();
        Long lastOrderId = po.getLastId() == null || po.getLastId() < 0 ? 0 : po.getLastId();
        List<OrderDetailDTO> list = orderClient.getHistoryOrders(getOrgId(), accountId, Strings.nullToEmpty(po.getSymbolId()),
                lastOrderId, po.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }

        for (OrderDetailDTO orderDetailDTO : list) {
            orderDetailDTO.setUserId(userId);
        }

        return ResultModel.ok(list);
    }
    @RequestMapping(value = "/order_trades", method = RequestMethod.POST)
    public ResultModel<List<TradeDetailDTO>> getOrderTrades(@RequestBody @Valid SimpleOrderPO po, AdminUserReply adminUser) {
        if ((po.getUserId() == null || po.getUserId() == 0)
                && StringUtils.isEmpty(po.getPhone()) && StringUtils.isEmpty(po.getEmail())) {
            return ResultModel.error("userId is null");
        }
        Long accountId = null;
        if (hasUserQueryCondition(po)) {
            Combo2<Long, Long> combo2 = null;
                combo2 = getUserIdAndMarginAccountId(po, adminUser.getOrgId());
            if (combo2 == null) {
                return ResultModel.ok(new ArrayList<>());
            }
            accountId = combo2.getV2();
        }
        List<TradeDetailDTO> trades = orderClient.getCoidOrderTrades(adminUser.getOrgId(), accountId, po.getOrderId(), Strings.nullToEmpty(po.getSymbolId()));
        return ResultModel.ok(trades);
    }
    @RequestMapping(value = "/trades", method = RequestMethod.POST)
    public ResultModel<List<TradeDetailDTO>> getTrades(@RequestBody @Valid SimpleOrderPO po, AdminUserReply adminUser) {
        if ((po.getUserId() == null || po.getUserId() == 0)
                && StringUtils.isEmpty(po.getPhone()) && StringUtils.isEmpty(po.getEmail())) {
            return ResultModel.error("userId is null");
        }
        Long accountId = null;
        if (hasUserQueryCondition(po)) {
            Combo2<Long, Long> combo2 = getUserIdAndMarginAccountId(po, adminUser.getOrgId());
            if (combo2 == null) {
                return ResultModel.ok(new ArrayList<>());
            }
            accountId = combo2.getV2();
        }

        Long lastTradeId = po.getLastId() == null || po.getLastId() < 0 ? 0 : po.getLastId();
        List<TradeDetailDTO> trades = orderClient.getCoinTrades(adminUser.getOrgId(), accountId,
                Strings.nullToEmpty(po.getSymbolId()), lastTradeId, po.getPageSize());
        if (CollectionUtils.isEmpty(trades)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(trades);
    }
}
