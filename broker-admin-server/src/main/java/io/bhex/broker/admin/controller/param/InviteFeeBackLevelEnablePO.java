package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2018/11/6 下午3:35
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Data
public class InviteFeeBackLevelEnablePO {

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long actId;

    private Integer status;

    private Integer coinStatus;

    private Integer futuresStatus;
}
