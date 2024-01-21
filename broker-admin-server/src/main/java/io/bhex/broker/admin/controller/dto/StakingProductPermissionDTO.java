package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class StakingProductPermissionDTO {
    private Integer allowFixed;
    private Integer allowFixedLock;
    private Integer allowCurrent;
}
