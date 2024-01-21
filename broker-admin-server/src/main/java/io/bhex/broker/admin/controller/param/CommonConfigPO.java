package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommonConfigPO {

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private String key;
    private String desc;
    private String value;

}
