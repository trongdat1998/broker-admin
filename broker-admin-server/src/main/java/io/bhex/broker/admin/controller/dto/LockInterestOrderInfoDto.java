package io.bhex.broker.admin.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LockInterestOrderInfoDto {

    private String orderId; //订单ID
    private String purchaseToken; // 支付tokenId
    private Long purchaseTime; //购买时间
    private String amount; //购买金额
    private String price; //份数
    private String projectName; //活动名称
    private String orderQuantity; //下单数量
    private String purchaseTokenName; //支付tokenName
    private String userId; //用户ID
    private String receiveTokenId;
    private String receiveTokenName;
    private String receiveTokenQuantity;
    private Long mappingId;
    private String useAmount;
    private String backAmount;
}
