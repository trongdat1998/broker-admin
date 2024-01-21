package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryDiscountFeeConfigPO {

    private Long discountId;

    private Long exchangeId;

    private String symbolId;
}
