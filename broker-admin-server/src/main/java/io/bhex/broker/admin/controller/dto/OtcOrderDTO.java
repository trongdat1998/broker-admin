package io.bhex.broker.admin.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单表
 *
 * @author lizhen
 * @date 2018-09-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OtcOrderDTO {
    /**
     * ID
     */
    private String id;
    /**
     * 订单类型 0-买入；1-卖出
     */
    private Integer side;
    /**
     * 商品id
     */
    private String itemId;
    /**
     * maker id
     */
    private String accountId;
    /**
     * maker 用户昵称
     */
    private String nickName;
    /**
     * taker id
     */
    private String targetAccountId;
    /**
     * taker 用户昵称
     */
    private String targetNickName;
    /**
     * 币种
     */
    private String tokenId;
    /**
     * 法币币种
     */
    private String currencyId;
    /**
     * 成交单价
     */
    private String price;
    /**
     * 成交数量
     */
    private String quantity;
    /**
     * 订单金额
     */
    private String amount;
    /**
     * 付款参考号
     */
    private String payCode;
    /**
     * 付款方式
     */
    private Integer paymentType;
    /**
     * 转账日期
     */
    private Long transferDate;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Long createDate;

    /**
     * 最后处理时间
     */
    private Long remainSeconds;

    private String remark;

    /**
     * 是否共享深度
     */
    private Boolean depthShare;

    /**
     * 交易对方所属券商id
     */
    private Long dealBrokerId;

    //1=用户,2=商户
    private Integer brokerType;

    /**
     * 申诉类型
     */
    private Integer appealType;
    /**
     * 申诉内容
     */
    private String appealContent;
}