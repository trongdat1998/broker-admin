package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
public class EditTokenExtraConfigPO {

    @NotEmpty
    @TokenValid
    private String tokenId;

    private Map<String, String> configs;

}
