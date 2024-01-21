package io.bhex.broker.admin.controller.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-11-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnfrozenUserVotePo {
    @NotNull
    private Long voteId;
    @NotNull
    private Integer authType;
    @NotNull
    private String verifyCode;
}
