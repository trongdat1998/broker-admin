package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SetInterestConfigPO {
    @NotNull
    String tokenId;
    @NotNull
    String interest;
    Integer interestPeriod;
    Integer calculationPeriod;
    Integer settlementPeriod;

}
