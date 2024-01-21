package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StakingProductRepaymentScheduleDTO {
    private Long userId;
    private Long productId;
    private Long orderId;
    private Integer sort;
    private Long rebateDate;
    private String rebateRate;
    private String rebateAmount;
    private String tokenId;
    private String tokenName;
    private Integer status;

}
