package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.*;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;

@Data
public class OdsQueryPo {
    @NotEmpty
    @CommonInputValid
    private String group;

    @Max(value = 500)
    private int limit;

    @DateStringValid(allowEmpty = true)
    private String startTime;
    @DateStringValid(allowEmpty = true)
    private String endTime;

    @TokenValid(allowEmpty = true)
    private String token;

    @SymbolValid(allowEmpty = true)
    private String symbol;

    @StringInValid(value = {"d", "H"})
    private String timeUnit;
}
