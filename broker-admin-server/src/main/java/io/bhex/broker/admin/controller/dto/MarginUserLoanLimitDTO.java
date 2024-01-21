package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class MarginUserLoanLimitDTO {

    private Long id;
    private Long orgId;
    private Long userId;
    private Long accountId;
    private String tokenId;
    private String limitAmount;
    private Integer status;
    private Long created;
    private Long updated;
}
