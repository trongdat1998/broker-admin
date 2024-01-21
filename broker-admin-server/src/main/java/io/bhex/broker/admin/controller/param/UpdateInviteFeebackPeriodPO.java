package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2019/1/15 下午12:00
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class UpdateInviteFeebackPeriodPO {

    @NotNull
    private Boolean fixed;

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @Min(value = 0)
    private Integer fixedTimeInMonth;

}
