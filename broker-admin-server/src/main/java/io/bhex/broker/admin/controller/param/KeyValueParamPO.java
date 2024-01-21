package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class KeyValueParamPO {

    @NotNull
    private String key;
    @NotNull
    private String value;
}
