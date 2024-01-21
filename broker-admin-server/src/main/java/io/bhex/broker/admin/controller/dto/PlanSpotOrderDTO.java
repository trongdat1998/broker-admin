package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.ToString;

import java.math.BigDecimal;


/**
 * @author wangsc
 * @description
 * @date 2020-08-20 16:44
 */
@ToString
public class PlanSpotOrderDTO {
    //时间
    private Long time;
    // 订单ID
    private Long orderId;

    private Long executedOrderId;
    //账户ID
    private Long accountId;
    //账户类型
    private Integer accountType;
    private Long userId;
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
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal price;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal quantity;
    //市价买入的是额
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal amount;
    //成交量
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal executedQty;
    //成交量
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal executedAmount;
    //买卖方向
    private Integer side;
    //状态标识
    private Integer status;
    //状态标识
    private String statusDesc;

    private Long exchangeId;

    private Long orgId;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal triggerPrice;

    private Long triggerTime;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal quotePrice;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal executedPrice;

    private Integer orderType;

    private Long updated;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSide() {
        return side;
    }

    public void setSide(Integer side) {
        this.side = side;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getExecutedOrderId() {
        return executedOrderId;
    }

    public void setExecutedOrderId(Long executedOrderId) {
        this.executedOrderId = executedOrderId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public String getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(String symbolId) {
        this.symbolId = symbolId;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public String getBaseTokenId() {
        return baseTokenId;
    }

    public void setBaseTokenId(String baseTokenId) {
        this.baseTokenId = baseTokenId;
    }

    public String getBaseTokenName() {
        return baseTokenName;
    }

    public void setBaseTokenName(String baseTokenName) {
        this.baseTokenName = baseTokenName;
    }

    public String getQuoteTokenId() {
        return quoteTokenId;
    }

    public void setQuoteTokenId(String quoteTokenId) {
        this.quoteTokenId = quoteTokenId;
    }

    public String getQuoteTokenName() {
        return quoteTokenName;
    }

    public void setQuoteTokenName(String quoteTokenName) {
        this.quoteTokenName = quoteTokenName;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Long getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getExecutedQty() {
        return executedQty;
    }

    public void setExecutedQty(BigDecimal executedQty) {
        this.executedQty = executedQty;
    }

    public BigDecimal getExecutedAmount() {
        return executedAmount;
    }

    public void setExecutedAmount(BigDecimal executedAmount) {
        this.executedAmount = executedAmount;
    }

    public Long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public BigDecimal getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(BigDecimal triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public BigDecimal getQuotePrice() {
        return quotePrice;
    }

    public void setQuotePrice(BigDecimal quotePrice) {
        this.quotePrice = quotePrice;
    }

    public BigDecimal getExecutedPrice() {
        return executedPrice;
    }

    public void setExecutedPrice(BigDecimal executedPrice) {
        this.executedPrice = executedPrice;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }
}
