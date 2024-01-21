package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @author wangsc
 * @description
 * @date 2020-08-22 16:51
 */
@Data
public class BatchCancelPlanSpotOrderPO {
    private Long userId;

    private String nationalCode;

    private String email;

    private String phone;

    private String symbolId = "";

    private String orderSide;

    private Integer accountType;

}
