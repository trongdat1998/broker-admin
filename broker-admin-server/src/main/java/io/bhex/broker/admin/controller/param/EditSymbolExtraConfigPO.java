package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
public class EditSymbolExtraConfigPO {

    @NotEmpty
    private String symbolId;

    private Map<String, String> configs;

}
