package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class QueryPagePO {
    @NotNull
    @Min(value = 1)
    private Integer current;

    @NotNull
    @Min(value = 1)
    @Max(value = 500)
    private Integer pageSize;

    private Long userId;
}
