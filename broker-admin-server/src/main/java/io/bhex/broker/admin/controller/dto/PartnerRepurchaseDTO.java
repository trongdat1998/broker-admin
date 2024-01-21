package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class PartnerRepurchaseDTO {

    private Long id;

    private Long orgId;

    private Long userId;

    private Long accountId;

    private Long activeAccountId;

    private String tokenId;

    private String amount;

    private Integer type;

    private Long lastTime;

    private Long clientOrderId;

    private Integer transferStatus;

    private Integer status;

    private String createTime;

    private String releaseAmount;

    private Long releaseTime;

    private String remark;

    private Integer underweightStatus;

    private Long underweightTime;
}
