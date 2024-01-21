package io.bhex.broker.admin.controller.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCrossLoanOrderPO {
    private Long userId;
    private Long accountId;
    private String tokenId;
    private Long loanId;
    private Integer status;
    private Long fromLoanId;
    private Long endLoanId;
    private Integer limit;

}
