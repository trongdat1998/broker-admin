package io.bhex.broker.admin.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lizhen
 * @date 2018-11-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OtcItemDTO {

    /**
     * ID
     */
    private String id;
    /**
     * 账户ID
     */
    private String accountId;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 币种
     */
    private String tokenId;
    /**
     * 法币币种
     */
    private String currencyId;
    /**
     * 广告类型 0.买入 1.卖出
     */
    private Integer side;
    /**
     * 定价类型 1-固定价格；2-浮动价格
     */
    private Integer priceType;
    /**
     * 单价
     */
    private String price;
    /**
     * 溢价比例 -5 - 5
     */
    private String premium;
    /**
     * 剩余数量
     */
    private String lastQuantity;
    /**
     * 数量
     */
    private String quantity;
    /**
     * 冻结数量(未成交订单中数量)
     */
    private String frozenQuantity;
    /**
     * 已成交数量
     */
    private String executedQuantity;
    /**
     * 单笔最小交易额（钱）
     */
    private String minAmount;
    /**
     * 单笔最大交易额（钱）
     */
    private String maxAmount;
    /**
     * 交易说明
     */
    private String remark;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Long createDate;
    /**
     * 订单数量
     */
    private Integer orderNum;
    /**
     * 完成数量
     */
    private Integer finishNum;
    /**
     * 订单数量
     */
    private Integer recentOrderNum;
    /**
     * 完成数量
     */
    private Integer recentExecuteRate;
}