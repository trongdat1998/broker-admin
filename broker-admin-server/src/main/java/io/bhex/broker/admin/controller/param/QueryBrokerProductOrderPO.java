package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryBrokerProductOrderPO {

    private String phone;

    private String email;

    private Long userId;

    private Long orderId;

    @NotNull
    private Long productId;

    private Long startId;

    private Integer limit;
}
