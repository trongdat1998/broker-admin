package io.bhex.broker.admin.controller.param;

import lombok.Data;

@Data
public class GetUserInviteInfoPO {

    private Long userId;

    private Long accountId;

    private String phone;

    private String email;

}
