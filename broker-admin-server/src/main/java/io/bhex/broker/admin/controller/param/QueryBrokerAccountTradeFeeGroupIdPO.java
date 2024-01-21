package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2018/11/21 下午5:38
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class QueryBrokerAccountTradeFeeGroupIdPO {

    @NotNull
    private Long groupId;

}
