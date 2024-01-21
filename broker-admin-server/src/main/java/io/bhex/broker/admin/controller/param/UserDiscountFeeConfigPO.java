package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDiscountFeeConfigPO {

    private Long orgId;

    private Long userId;

    private Long discountId;

    private Integer isBase;
}
