package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryLoginLogsPO {
    @NotNull
    private Integer current;

    @NotNull
    private Integer pageSize;

    @NotNull
    private Long userId;
}
