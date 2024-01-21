package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.IntInValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CancelOtcItemPO {

    @NotNull
    private Long itemId;
    @NotNull
    private Long accountId;

    @NotNull
    private Integer authType;

    @NotEmpty
    private String verifyCode;



}
