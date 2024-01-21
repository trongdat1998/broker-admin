package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LockBalanceLogDTO {

    private Long id;

    private Long brokerId;

    private String userId;

    private String accountId;

    private Integer type;

    private Integer status;

    private String tokenId;

    private String amount;

    private String unlockAmount;

    private String lastAmount;

    private Long operator;

    private Long clientOrderId;

    private Integer subjectType;

    private String mark;

    private String createTime;

    private String updateTime;
}
