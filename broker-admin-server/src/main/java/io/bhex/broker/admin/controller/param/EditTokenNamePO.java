package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class EditTokenNamePO {

    @NotEmpty
    @TokenValid
    private String tokenId;

    @NotEmpty
    private String tokenName;
}
