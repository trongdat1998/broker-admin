package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SetMarginSymbolPO {
    @NotNull
    String symbolId;
    @NotNull
    Boolean allowMargin;
}
