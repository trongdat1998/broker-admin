package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryHobbitLeaderPO {

    private Long id;

    private Long userId;

    private Integer lastId;

    private Integer endId;

    @NotNull
    private Integer pageSize;

    private Boolean isApplying = false;

    private Boolean quitApply = false;
}
