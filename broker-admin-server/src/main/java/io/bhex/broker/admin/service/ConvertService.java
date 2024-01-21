package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.ConvertOrderDTO;
import io.bhex.broker.admin.controller.dto.ConvertSymbolDTO;
import io.bhex.broker.admin.controller.dto.FundAccountDTO;
import io.bhex.broker.admin.controller.param.ConvertOrderQueryPO;
import io.bhex.broker.admin.controller.param.ConvertSymbolModifyPO;
import io.bhex.broker.admin.controller.param.ConvertSymbolCreatePO;
import io.bhex.broker.admin.controller.param.ConvertSymbolStatusUpdatePO;
import io.bhex.broker.grpc.convert.AddConvertSymbolResponse;
import io.bhex.broker.grpc.convert.ModifyConvertSymbolResponse;
import io.bhex.broker.grpc.convert.UpdateConvertSymbolStatusResponse;

import java.util.List;

public interface ConvertService {

    AddConvertSymbolResponse addConvertSymbol(ConvertSymbolCreatePO po, Long orgId);

    List<ConvertSymbolDTO> queryConvertSymbol(Long orgId);

    ModifyConvertSymbolResponse modifyConvertSymbol(ConvertSymbolModifyPO po, Long orgId);

    UpdateConvertSymbolStatusResponse updateConvertSymbolStatus(ConvertSymbolStatusUpdatePO po, Long orgId);

    List<ConvertOrderDTO> queryConvertOrders(ConvertOrderQueryPO po, Long orgId);

    List<FundAccountDTO> queryFundAccountShow(Long orgId);
}
