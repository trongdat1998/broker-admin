package io.bhex.broker.admin.controller.param;

import lombok.Data;

@Data
public class SimpleOrderPO {

    private Long userId;

    private String nationalCode;

    private String email;

    private String baseTokenId;

    private String quoteTokenId;

    private String symbolId;

    private Long orderId;

    private Long lastId;

    private Long startTime;

    private Long endTime;

    private Integer pageSize = 20;

    private String phone;

    private Integer accountType;
}
