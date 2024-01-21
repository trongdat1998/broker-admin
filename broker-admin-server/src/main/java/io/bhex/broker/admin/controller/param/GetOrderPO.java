package io.bhex.broker.admin.controller.param;

import lombok.Data;

@Data
public class GetOrderPO {

    private Long userId;

    private String nationalCode;

    private String mobile;

    private String email;
}
