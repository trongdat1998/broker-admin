package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryCurrentProductAssetPO {

    private String phone;

    private String email;

    private Long userId;

    @NotNull
    private Long productId;

    private Long startId;

    private Integer limit;
}
