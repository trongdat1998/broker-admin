package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryLiquidationPositionPO extends GetBrokerUserPO {

    @NotNull
    private Long orderId;
}
