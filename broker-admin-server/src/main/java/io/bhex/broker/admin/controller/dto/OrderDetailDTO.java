package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@ToString
public class OrderDetailDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

   // private Long accountId;

    //private Long clientOrderId;

    //private Long matchSequenceId;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal averagePrice;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal price;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal quantity;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal amount;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal locked;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal executedQuantity;
    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal executedAmount;
    private String symbolId;
//    private Long exchangeId;
//    private Long matchExchangeId;
    private Integer status;
    private Integer side;
    private Integer orderType;
//    private Integer timeInForce;
//    private String stopPrice;
//    private String icebergQuantity;
    private Long created;
    private Long updated;
 //   private String settleLock;
}
