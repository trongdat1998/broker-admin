package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InviteRelationPO {

   // @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long inviteUserId;

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long invitedUserId;
}
