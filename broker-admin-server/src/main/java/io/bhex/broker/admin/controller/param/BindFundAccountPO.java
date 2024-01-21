package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BindFundAccountPO {

    @NotNull
    private Long userId;

    @NotNull
    private Long accountId;

    @NotNull
    private Integer authType;

    /**
     * 标签
     */
    @NotEmpty
    private String tag;

    /**
     * 说明
     */
    private String remark;

    private Long requestId;

    @NotNull
    private String verifyCode;

}
