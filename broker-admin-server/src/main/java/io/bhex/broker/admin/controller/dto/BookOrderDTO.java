package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Date: 2019/10/14 下午2:54
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class BookOrderDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal price;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal quantity;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal amount;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long accountId;

    @JsonSerialize(using = ToStringSerializer.class)
    private String brokerUserId;

    private Long brokerId;

    private String brokerName;

    private Long exchangeId;

    @JsonIgnore
    public Double getQuantityDouble() {
        return quantity.doubleValue();
    }
    @JsonIgnore
    public Double getAmountDouble() {
        return amount.doubleValue();
    }

}
