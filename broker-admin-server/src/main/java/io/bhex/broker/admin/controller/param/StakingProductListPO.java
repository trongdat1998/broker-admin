package io.bhex.broker.admin.controller.param;

import lombok.Data;

import java.util.Objects;

@Data
public class StakingProductListPO {

    private Integer productType;
    private Long lastId;
    private Integer limit;

    public Long getLastId() {
        if (Objects.isNull(lastId)) {
            return 0L;
        }
        return lastId;
    }

    public Integer getLimit() {
        if (Objects.isNull(limit)) {
            return 20;
        }
        return limit;
    }
}
