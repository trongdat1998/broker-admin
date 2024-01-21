/**********************************
 *@项目名称: broker-parent
 *@文件名称: io.bhex.broker.domain
 *@Date 2018/6/26
 *@Author peiwei.ren@bhex.io 
 *@Copyright（C）: 2018 BlueHelix Inc.   All rights reserved.
 *注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 ***************************************/
package io.bhex.broker.admin.controller.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class OrderDTO {

    //时间
    private Long time;
    // 订单ID
    private Long orderId;
    //账户ID
    private Long accountId;
    //账户类型
    private Integer accountType;
    private String clientOrderId;
    //币对Id
    private String symbolId;
    //币对Name
    private String symbolName;
    private String baseTokenId;
    private String baseTokenName;
    private String quoteTokenId;
    private String quoteTokenName;
    //下单价格
    private String price;
    //原始下单数量
    private String origQty;
    //成交量
    private String executedQty;
    //成交量
    private String executedAmount;
    // 成交均价
    private String avgPrice;
    //订单类型
    private String type;
    //买卖方向
    private String side;
    private @Singular("fee")
    List<OrderMatchFeeDTO> fees;
    //状态标识
    private String status;
    //状态标识
    private String statusDesc;

    private String lastExecutedQuantity;
    private String lastExecutedPrice;
    private String commissionAmount;
    private String commissionAsset;

    //未成交数量
    private String noExecutedQty;
    //下单总金额
    private String amount;
}
