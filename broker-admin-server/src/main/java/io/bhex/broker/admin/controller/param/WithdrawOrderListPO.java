package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.IntInValid;
import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class WithdrawOrderListPO {

    private Long userId = 0L;

    private String nationalCode = "";

    private String mobile = "";
    private String phone = "";
    private String email = "";

    private Long fromId = 0L;

    private Long lastId = 0L;

    private Boolean next = false;

    @Max(value = 500)
    private Integer pageSize = 30;

    private String tokenId;

    private Long startTime = 0L;

    private Long endTime = 0L;

    private String address;

    private String txId;

}
