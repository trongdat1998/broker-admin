package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskConfigDTO {

    private Long id;
    private Long orgId;
    private String withdrawLine;
    private String warnLine;
    private String appendLine;
    private String stopLine;
    private String maxLoanLimit;
    private Integer notifyType;
    private String notifyNumber;
    private Long created;
    private Long updated;
    private String maxLoanLimitVip1;
    private String maxLoanLimitVip2;
    private String maxLoanLimitVip3;


}
