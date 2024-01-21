package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import io.bhex.bhop.common.util.validation.IntInValid;
import io.bhex.bhop.common.util.validation.SymbolValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-08-18
 */
@Data
public class OtcThirdPartyDisclaimerQueryPO {

    @NotNull
    private Long thirdPartyId;

    private String language;

}
