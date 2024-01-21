package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 20/11/2018 6:01 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long accountId;

    private String tokenId;

    private String tokenName;

    private String address;

    private String fromAddress;

    private String tokenQuantity;

    private String txid;

    private String statusCode;

    private String statusDesc;

    private Long time;
    private Long createTime;
    private String addressExt;

    private String fromAddressExt;

    /**
     * 入账类型
     */
    private Integer receiptType;

    /**
     * 不能入账的原因
     */
    private Integer cannotReceiptReason;

    private Boolean isBaas = false;
}
