package io.bhex.broker.admin.controller.param;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ModifyHobbitDailyPO {

    private Long id;

    private BigDecimal feeTotal;

    private BigDecimal saasTotal;

    private BigDecimal marketCost;

    private BigDecimal inviteTotal;
}
