package io.bhex.broker.admin.grpc.client;

import io.bhex.base.admin.common.*;
import io.bhex.broker.admin.controller.param.UpdateBrokerTradeFeePO;

/**
 * @Description:
 * @Date: 2018/10/31 下午4:53
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface BrokerAccountTradeFeeSettingClient {


    EditBrokerAccountTradeFeeGroupResponse editBrokerAccountTradeFeeGroup (EditBrokerAccountTradeFeeGroupRequest request);

    EnableBrokerAccountTradeFeeGroupResponse enableBrokerAccountTradeFeeGroup(EnableBrokerAccountTradeFeeGroupRequest request);

    DisableBrokerAccountTradeFeeGroupResponse disableBrokerAccountTradeFeeGroup(DisableBrokerAccountTradeFeeGroupRequest request);

    GetBrokerAccountTradeFeeGroupResponse getBrokerAccountTradeFeeGroup(GetBrokerAccountTradeFeeGroupRequest request);

    GetBrokerAccountTradeFeeGroupsResponse getBrokerAccountTradeFeeGroups(GetBrokerAccountTradeFeeGroupsRequest request);

    //GetExistedAccountIdsResponse getExistedAccountIds(GetExistedAccountIdsRequest request);
    SaveBrokerAccountTradeFeeDetailsResponse saveBrokerAccountTradeFeeDetails(SaveBrokerAccountTradeFeeDetailsRequest request);


    UpdateSendStatusResponse updateSendStatus(UpdateSendStatusRequest request);

    /**
     * 平台更新用户费率配置(独立部署--需要补全baseRequest)
     *
     * @param request
     * @return
     */
    UpdateAccountFeeRateAdjustResponse updateAccountFeeRateAdjust(UpdateAccountFeeRateAdjustRequest request);

    /**
     * 平台删除用户费率配置
     *
     * @param request
     * @return
     */
    DeleteAccountFeeRateAdjustResponse deleteAccountFeeRateAdjust(DeleteAccountFeeRateAdjustRequest request);
}
