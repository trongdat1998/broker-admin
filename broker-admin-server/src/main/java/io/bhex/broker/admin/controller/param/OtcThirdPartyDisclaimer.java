package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-08-18
 */
@Data
public class OtcThirdPartyDisclaimer {

    @NotNull
    private Long thirdPartyId;

    @NotEmpty
    private String language;

    @NotNull
    private String disclaimer;
}
