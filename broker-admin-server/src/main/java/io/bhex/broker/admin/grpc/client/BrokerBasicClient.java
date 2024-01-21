package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.ListCurrencyRequest;
import io.bhex.broker.grpc.admin.ListCurrencyResponse;
import io.bhex.broker.grpc.basic.QuerySymbolRequest;
import io.bhex.broker.grpc.basic.QuerySymbolResponse;
import io.bhex.broker.grpc.common.Header;

public interface BrokerBasicClient {

    QuerySymbolResponse querySymbols(QuerySymbolRequest request);

    ListCurrencyResponse queryCurrencies(ListCurrencyRequest request);
}
