package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.QueryFundAccountShowRequest;
import io.bhex.broker.grpc.admin.QueryFundAccountShowResponse;
import io.bhex.broker.grpc.convert.*;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: cookie.yuan
 * @CreateDate: 18/08/2020
 * @Copyright（C）: 2020 BHEX Inc. All rights reserved.
 */
public interface ConvertClient {

    AddConvertSymbolResponse addConvertSymbol(AddConvertSymbolRequest request);

    GetConvertSymbolsResponse getConvertSymbols(GetConvertSymbolsRequest request);

    ModifyConvertSymbolResponse modifyConvertSymbol(ModifyConvertSymbolRequest request);

    UpdateConvertSymbolStatusResponse updateConvertSymbolStatus(UpdateConvertSymbolStatusRequest request);

    AdminQueryConvertOrdersResponse queryConvertOrders(AdminQueryConvertOrdersRequest request);

    QueryFundAccountShowResponse queryFundAccountShow(QueryFundAccountShowRequest request);
}
