package io.bhex.broker.admin.controller.param;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ActivityInfoPO {

    @NotNull
    private Long projectId;
}
