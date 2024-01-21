package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2019/10/14 下午3:50
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class QueryBrokerOrderPO {

    @NotNull
    private Integer side; //0-buy 1-sell
    @NotEmpty
    private String symbolId;
    @NotNull
    private Integer type; //1-用户模式 2-盘口模式
}
