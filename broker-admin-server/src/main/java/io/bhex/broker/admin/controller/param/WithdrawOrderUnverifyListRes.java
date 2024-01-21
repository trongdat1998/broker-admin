package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawOrderUnverifyListRes {

    private Long created;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long withdrawOrderId; //此orderid是平台id

    private String tokenId;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal quantity;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal quantityInUsdt;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal arrivalQuantity;

    private String address;

    private Integer failedReason;
    private String failedReasonDesc;

    private Long withdrawalTime;

    private Long finishedTime;

    private Long walletHandleTime;

    private Long updateTime;
}
