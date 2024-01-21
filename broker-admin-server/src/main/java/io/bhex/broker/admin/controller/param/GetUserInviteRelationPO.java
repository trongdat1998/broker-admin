package io.bhex.broker.admin.controller.param;

import lombok.Data;

@Data
public class GetUserInviteRelationPO {

    private Long userId;

    private Long accountId;

    private String phone;

    private String email;

    private Long fromId;

    private Long lastId;

    private Long startTime;

    private Long endTime;

    private Integer limit;

}
