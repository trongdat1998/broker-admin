package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryLevelUserPO {

    @NotNull
    private Long levelConfigId;
    @NotNull
    private Long lastId;
    @NotNull
    private Integer pageSize;

    private Boolean queryWhiteList = false;
}
