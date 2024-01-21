package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestConfigDTO {

    private Long id;
    private Long orgId;
    private String tokenId;
    private String interest;
    private Long interestPeriod;
    private Long calculationPeriod;
    private Long settlementPeriod;
    private Long levelConfigId;
    private Long created;
    private Long updated;
    private String showInterest;

}
