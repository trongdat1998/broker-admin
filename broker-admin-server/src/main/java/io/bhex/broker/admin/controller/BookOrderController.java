package io.bhex.broker.admin.controller;

import io.bhex.base.account.BookOrderStruct;
import io.bhex.base.account.GetBookOrdersRequest;
import io.bhex.base.account.GetBookOrdersResponse;
import io.bhex.base.proto.OrderSideEnum;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BookOrderDTO;
import io.bhex.broker.admin.controller.dto.ContractExchangeInfo;
import io.bhex.broker.admin.controller.param.QueryBrokerOrderPO;
import io.bhex.broker.admin.controller.param.SimpleOrderPO;
import io.bhex.broker.admin.grpc.client.OrderClient;
import io.bhex.broker.admin.service.ExchangeContractService;
import io.bhex.broker.grpc.common.AccountTypeEnum;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.common.Platform;
import io.bhex.broker.grpc.order.CancelOrderRequest;
import io.bhex.broker.grpc.order.CancelOrderResponse;
import io.bhex.broker.grpc.order.OrderResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: broker user 下单管理controller
 * @Date: 2018/8/29 下午6:09
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/book_order")
public class BookOrderController extends BrokerBaseController {


    @Autowired
    private OrderClient orderClient;

    @Autowired
    private ExchangeContractService exchangeContractService;

    @BussinessLogAnnotation(opContent = "Cancel Order OrderId:{#po.orderId} ")
    @RequestMapping(value = "/cancel_order", method = RequestMethod.POST)
    public ResultModel<Boolean> cancelOrder(@RequestBody @Valid SimpleOrderPO po) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
        Long accountId = combo2.getV2();
        Header header = Header.newBuilder()
                .setOrgId(getOrgId())
                .setUserId(combo2.getV1())
                .setAdminUid(getRequestUserId())
                .setPlatform(Platform.PC)
                .setSource("broker-admin")
                .build();
        CancelOrderRequest request = CancelOrderRequest.newBuilder()
                .setHeader(header)
                .setAccountId(accountId)
                .setAccountType(AccountTypeEnum.COIN)
                .setOrderId(po.getOrderId())
                .setOrderResponseType(OrderResponseType.FULL)
                .build();
        CancelOrderResponse response = orderClient.cancelOrder(request);
        log.info("cancelOrder req:{} res:{}", request, response);
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("cancel order failed!");
    }



    @RequestMapping(value = "/book_orders")
    public ResultModel getBookOrders(@RequestBody @Valid QueryBrokerOrderPO param) {
        Long orgId = getOrgId();

        List<ContractExchangeInfo> contractExchangeInfos = exchangeContractService.listALlExchangeContractInfo(orgId);
        if (CollectionUtils.isEmpty(contractExchangeInfos)) {
            return ResultModel.error("no contract exchange");
        }
        List<Long> exchangeIds = contractExchangeInfos.stream().map(info -> info.getExchangeId()).collect(Collectors.toList());

        GetBookOrdersRequest request = GetBookOrdersRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setExchangeId(exchangeIds.get(0))
                .setSymbolId(param.getSymbolId().replace("/", ""))
                .setSide(OrderSideEnum.forNumber(param.getSide()))
                .build();

        GetBookOrdersResponse response = orderClient.getBookOrders(request);
        log.info("response:{}", response.getResponseCode());

        Map<String, Object> result = new HashMap<>();
        if (response.getResponseCode() != GetBookOrdersResponse.GetBookOrdersResponseEnum.SUCCESS) {
            result.put("result", response.getResponseCodeValue());
            return ResultModel.ok(result);
        }
        result.put("result", 0);

        List<BookOrderStruct> list = response.getBookOrdersList();
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(result);
        }

        List<BookOrderDTO> resultList = list.stream()
                .filter(o -> o.getBrokerId() == orgId)
                .map(o -> {
                    BookOrderDTO dto = new BookOrderDTO();
                    BeanUtils.copyProperties(o, dto);
                    dto.setAmount(new BigDecimal(o.getAmount()));
                    dto.setPrice(new BigDecimal(o.getPrice()));
                    dto.setQuantity(new BigDecimal(o.getQuantity()));
                    return dto;
                }).collect(Collectors.toList());

        if (param.getType() == 1) {
            result.put("list", resultList);
            return ResultModel.ok(result);
        }

        //盘口聚合模式
        List<BookOrderDTO> groupResult = new ArrayList<>();
        Map<BigDecimal, List<BookOrderDTO>> groups = resultList.stream().collect(Collectors.groupingBy(BookOrderDTO::getPrice));
        for (BigDecimal price : groups.keySet()) {
            List<BookOrderDTO> orders = groups.get(price);
            BookOrderDTO dto = new BookOrderDTO();
            dto.setPrice(price);
            dto.setQuantity(new BigDecimal(orders.stream().mapToDouble(BookOrderDTO::getQuantityDouble).sum()));
            dto.setAmount(new BigDecimal(orders.stream().mapToDouble(BookOrderDTO::getAmountDouble).sum()));
            groupResult.add(dto);
        }

        Comparator<BookOrderDTO> comparator = Comparator.comparing(BookOrderDTO::getPrice);
        if (param.getSide() == OrderSideEnum.SELL_VALUE) {
            comparator = comparator.reversed();
        }
        groupResult = groupResult.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        result.put("list", groupResult);
        return ResultModel.ok(result);
    }
}
