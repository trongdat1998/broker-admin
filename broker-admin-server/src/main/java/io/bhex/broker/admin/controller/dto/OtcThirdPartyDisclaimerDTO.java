package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtcThirdPartyDisclaimerDTO {

    private Long orgId;
    private Long thirdPartyId;
    private String language;
    private String disclaimer;
    private Long created;
    private Long updated;

}
