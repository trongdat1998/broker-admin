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
 * @CreateDate: 20/11/2018 5:21 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawOrderDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long accountId;

    private String tokenId;
    private String tokenName;
    private String address;
    private String tokenQuantity;
    private String arriveQuantity;
    private String statusCode;
    private String statusDesc;
    private Long time;

    private Long verifyTime;

    private Long createTime;

    private Long updateTime;

    private Long walletHandleTime;
    private String addressExt;
    private String txid;
    private Boolean isChainWithdraw;

    private Boolean isBaas = false;
    private String chainType;

}
