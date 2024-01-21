package io.bhex.broker.admin.grpc.client;

import io.bhex.base.account.GetBrokerExchangeContractReply;
import io.bhex.base.account.GetBrokerExchangeContractRequest;
import io.bhex.broker.admin.controller.dto.DiscountFeeConfigDTO;
import io.bhex.broker.admin.controller.dto.DiscountFeeUserDTO;
import io.bhex.broker.admin.controller.dto.SymbolFeeConfigDTO;
import io.bhex.broker.admin.controller.dto.SymbolMarketAccountDTO;
import io.bhex.broker.admin.controller.param.DiscountFeeConfigPO;
import io.bhex.broker.admin.controller.param.SymbolMarketAccountDetailPO;
import io.bhex.broker.grpc.fee.*;

import java.util.List;

public interface BrokerFeeClient {

    AddDiscountFeeConfigResponse addDiscountFeeConfig(DiscountFeeConfigPO request);

    //获取券商折扣比例列表
    List<DiscountFeeConfigDTO> queryDiscountFeeConfigList(QueryDiscountFeeConfigRequest request);

    //查询单个券商折扣比例数据
    DiscountFeeConfigDTO queryOneDiscountFeeConfig(QueryOneDiscountFeeConfigRequest request);

    //保存用户折扣比例配置
    SaveUserDiscountConfigResponse saveUserDiscountConfig(SaveUserDiscountConfigRequest request);

    //取消用户手续费折扣
    CancelUserDiscountConfigResponse cancelUserDiscountConfig(CancelUserDiscountConfigRequest request);

    //获取用户手续费折扣配置信息
    DiscountFeeUserDTO queryUserDiscountConfig(QueryUserDiscountConfigRequest request);

    //创建修改symbol手续费配置
    AddSymbolFeeConfigResponse addSymbolFeeConfig(AddSymbolFeeConfigRequest request);

    //获取券商symbol手续费配置
    List<SymbolFeeConfigDTO> querySymbolFeeConfigList(QuerySymbolFeeConfigRequest request);

    SaveSymbolMarketAccountResponse saveSymbolMarketAccount(Long orgId, List<SymbolMarketAccountDetailPO> marketAccountList);

    List<SymbolMarketAccountDTO> queryAllSymbolMarketAccount(QueryAccountTradeFeeConfigRequest request);

    GetBrokerExchangeContractReply getBrokerExchangeContract(GetBrokerExchangeContractRequest request);

    DeleteAccountTradeFeeConfigResponse deleteSymbolMarketAccount(DeleteAccountTradeFeeConfigRequest request);
}
