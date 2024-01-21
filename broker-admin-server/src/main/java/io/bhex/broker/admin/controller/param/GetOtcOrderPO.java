package io.bhex.broker.admin.controller.param;

import javax.validation.constraints.NotNull;

import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

@Data
public class GetOtcOrderPO {

    private Long orderId;

    @NotNull
    private Integer current;

    @NotNull
    private Integer pageSize;

    //20=支付未确认,30=申述,40=取消,50=完成
    private Integer status;

    private Long userId;

    private Long startTime;

    private Long endTime;

    private String email;

    private String mobile;

    private Integer side;

    @TokenValid(allowEmpty = true)
    private String tokenId;

}
