package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2019/12/20 下午3:05
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class SymbolSwitchPO {

//    @NotNull
//    private Long exchangeId;
    @NotNull
    private String symbolId;
    @NotNull
    private Integer switchType;
    @NotNull
    private Boolean open;

    private String remark;
}
