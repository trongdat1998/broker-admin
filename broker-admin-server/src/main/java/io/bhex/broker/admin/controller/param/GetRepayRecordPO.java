package io.bhex.broker.admin.controller.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRepayRecordPO {
    private Long userId;
    private Long accountId;
    private Long loanOrderId;
    private String tokenId;
    private Long fromRepayId;
    private Long endRepayId;
    private Integer limit;


}
