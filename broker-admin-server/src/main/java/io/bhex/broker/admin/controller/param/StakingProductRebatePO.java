package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 理财产品派息PO
 * @author songxd
 * @date
 */
@Data
public class StakingProductRebatePO {
    private Long productId;
    private Long productRebateId;

    @NotNull
    private Integer authType;

    @NotEmpty
    private String verifyCode;
}
