package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;


@Data
public class WithdrawOrderVerifyListRes {

    private Long created;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long withdrawOrderId;

    private String tokenId;

    private String quantity;

    private String arrivalQuantity;

    private String address;

    private String  adminUserName;

    private String  remark;

    private long verifyTime;

    private Integer verifyStatus;

    private Integer failedReason;
    private String failedReasonDesc;
}
