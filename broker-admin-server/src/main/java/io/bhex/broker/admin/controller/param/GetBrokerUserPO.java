package io.bhex.broker.admin.controller.param;

import lombok.Data;

@Data
public class GetBrokerUserPO {

    private Long userId;

    private Long accountId;

    private String nationalCode;

    private String phone;

    private String email;



}
