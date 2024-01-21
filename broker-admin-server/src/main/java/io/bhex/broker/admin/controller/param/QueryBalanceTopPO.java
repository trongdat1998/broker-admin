package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class QueryBalanceTopPO {

    private Long userId = 0L;

    private String phone = "";

    private String email = "";

    @Max(value = 500)
    private Integer top = 100;

    @TokenValid(allowEmpty = true)
    private String tokenId;


}
