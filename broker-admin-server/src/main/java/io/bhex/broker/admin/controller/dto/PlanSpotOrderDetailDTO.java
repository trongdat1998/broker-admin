package io.bhex.broker.admin.controller.dto;

import lombok.ToString;


/**
 * @author wangsc
 * @description
 * @date 2020-08-20 16:44
 */
@ToString
public class PlanSpotOrderDetailDTO {
    private PlanSpotOrderDTO planOrder;
    private OrderDetailDTO order;

    public PlanSpotOrderDTO getPlanOrder() {
        return planOrder;
    }

    public void setPlanOrder(PlanSpotOrderDTO planOrder) {
        this.planOrder = planOrder;
    }

    public OrderDetailDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDetailDTO order) {
        this.order = order;
    }
}
