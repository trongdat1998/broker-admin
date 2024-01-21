package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeDetailDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tradeId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private Long createdAt;
    private String symbolId;
    private Integer side;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal price;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal quantity;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal amount;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal tokenFee;

    private Integer orderType;
    private String feeTokenId;

    private Long orderId;

    private String pnl;


    //private Long updatedAt;
}
