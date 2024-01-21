package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StakingProductProfileDTO {

    private Long id;
    private String productName;
    private String tokenId;
    private String tokenName;
    //派息方式0=分期付息 1=一次性还本付息
    private Integer dividendType;
    private Long subscribeStartDate;
    private Long subscribeEndDate;
    private Long interestStartDate;
    //类型:0=定期 1=定期锁仓 2=活期
    private Integer type;
    //发行总额度
    private String totalAmount;
    //已售额度
    private String soldAmount;
    private Integer status;
    private Integer isShow;
}
