package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetCurrentProductRebateListPO {

    @NotNull
    private Long productId;

    private Integer status;

    private Long startRebateDate;

    private Integer size;

    public Integer getStatus() {
        if (status == null) {
            return -1;
        }
        return status;
    }

    public Long getStartRebateDate() {
        if (startRebateDate == null) {
            return 0L;
        }
        return startRebateDate;
    }

    public Integer getSize() {
        if (size == null) {
            return 20;
        }

        if (size < 0 || size > 100) {
            return 100;
        }

        return size;
    }

}
