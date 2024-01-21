package io.bhex.broker.admin.controller.dto;

import lombok.*;

/**
 * 还币记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RepayRecordDTO {

    private Long repayOrderId;
    private Long orgId;
    private Long accountId;
    private String clientId;
    private Long balanceId;
    private String tokenId;
    private Long loanOrderId;
    private String amount;
    private String interest;
    private Long createdAt;


}
