package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class MarginLoanLimitDTO {

    private Long id;
    private Long orgId;
    private String tokenId;
    private String limitAmount;
    private Integer status;
    private Long created;
    private Long updated;
}
