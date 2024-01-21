package io.bhex.broker.admin.controller.param;

import lombok.Data;

@Data
public class UnlockBalancePO {
    private Long clientOrderId;
    private Long userId;
    private String token;
    private String unlockAmount;
    private String unlockReason;
}
