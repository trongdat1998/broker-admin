package io.bhex.broker.admin.controller.param;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author lizhen
 * @date 2018-11-11
 */
@Data
public class OtcWhiteListPO {

    private Long userId;

    @NotNull
    private Integer current;

    @NotNull
    private Integer pageSize;
}