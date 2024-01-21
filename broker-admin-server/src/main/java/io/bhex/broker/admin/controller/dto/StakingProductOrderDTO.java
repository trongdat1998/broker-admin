package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StakingProductOrderDTO {

    private Long orderId;

    private Long userId;

    private Long productId;

    private String productName;

    //派息方式0=分期付息 1=一次性还本付息
    private Integer dividendType;

    private String tokenId;

    private String tokenName;

    private Integer orderType;

    //期限
    private Integer timeLimit;

    //购买手数
    private Integer payLots;

    //支付金额
    private String payAmount;

    //生效计息日期
    private Long takeEffectDate;

    //产品到期时间
    private Long productEndDate;

    //赎回日期
    private Long redemptionDate;

    private Integer status;

    private String referenceApr;

    //创建日期
    private Long createdAt;
}
