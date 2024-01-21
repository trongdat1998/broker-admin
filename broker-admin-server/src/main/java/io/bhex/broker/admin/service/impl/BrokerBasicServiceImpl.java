package io.bhex.broker.admin.service.impl;

import com.google.common.collect.Lists;
import io.bhex.base.token.TokenCategory;
import io.bhex.broker.admin.controller.dto.SymbolDTO;
import io.bhex.broker.admin.grpc.client.BrokerBasicClient;
import io.bhex.broker.admin.service.BrokerBasicService;
import io.bhex.broker.grpc.admin.ListCurrencyRequest;
import io.bhex.broker.grpc.admin.ListCurrencyResponse;
import io.bhex.broker.grpc.basic.QuerySymbolRequest;
import io.bhex.broker.grpc.basic.QuerySymbolResponse;
import io.bhex.broker.grpc.common.Header;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BrokerBasicServiceImpl implements BrokerBasicService {

    @Resource
    private BrokerBasicClient brokerBasicClient;

    @Override
    public List<SymbolDTO> listSymbol(long brokerId, TokenCategory category) {

        QuerySymbolRequest request=QuerySymbolRequest.newBuilder()
                .addAllCategory(Lists.newArrayList(category.getNumber()))
                .setHeader(Header.newBuilder().setOrgId(brokerId).build())
                .build();

        QuerySymbolResponse resp=brokerBasicClient.querySymbols(request);
        return resp.getSymbolList().stream()
                .filter(i->i.getIsReverse()).filter(i->i.getOrgId()==brokerId)
                .map(i->SymbolDTO.builder()
                    .symbolId(i.getSymbolId())
                    .symbolName(i.getSymbolName())
                    .quoteTokenId(i.getQuoteTokenId())
                    .allowTrade(i.getCanTrade())
                    .banSellStatus(i.getBanSellStatus())
                    .basePrecision(i.getBasePrecision())
                    .baseTokenId(i.getBaseTokenId())
                    .minPricePrecision(i.getMinPricePrecision())
                    .minTradeAmount(i.getMinTradeAmount())
                    .minTradeQuantity(i.getMinTradeQuantity())
                    .exchangeId(i.getExchangeId())
                    .build()
        ).collect(Collectors.toList());

    }

    @Override
    public List<String> listCurrency(){
        ListCurrencyResponse resp= brokerBasicClient.queryCurrencies(ListCurrencyRequest.newBuilder().build());
        List<String> codes=Lists.newArrayList(resp.getCurrenciesList().stream().map(i->i.getCode()).collect(Collectors.toSet()));
        Collections.sort(codes);
        return codes;
    }
}
