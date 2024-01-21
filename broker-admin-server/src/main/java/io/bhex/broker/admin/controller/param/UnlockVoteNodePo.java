package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-11-16
 */
@Data
public class UnlockVoteNodePo {
    @NotNull
    private Long nodeId;
    @NotNull
    private Integer authType;
    @NotNull
    private String verifyCode;
}
