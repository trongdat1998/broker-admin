package io.bhex.broker.admin.controller.param;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class HandleOtcOrderPO {

    @NotNull
    private Long orderId;

    @NotNull
    private Integer type;

    private String remark;
}
