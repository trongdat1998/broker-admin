package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetSubUserAssetPO {
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long userId;

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long accountId;
}
