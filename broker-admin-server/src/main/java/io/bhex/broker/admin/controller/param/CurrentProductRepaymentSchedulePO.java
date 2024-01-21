package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CurrentProductRepaymentSchedulePO {

    @NotNull
    private Long userId;

    @NotNull
    private Long productId;

    private Long startId;

    private Integer size;

    public Long getStartId() {
        if (startId == null) {
            return 0L;
        }
        return startId;
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
