package io.bhex.broker.admin.controller.dto;

import lombok.*;

/**
 * 借贷记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CrossLoanOrderDTO {

    private Long loanOrderId;
    private String clientId;
    private Long orgId;
    private Long balanceId;
    private Long lenderAccountId;
    private Long lenderId;
    private String tokenId;
    private String loanAmount;
    private String repaidAmount;
    private String unpaidAmount;
    private String interestRate1;
    private Long interestStart;
    private Integer status;
    private String interestPaid;
    private String interestUnpaid;
    private Long createdAt;
    private Long updatedAt;
    private Long accountId;


}
