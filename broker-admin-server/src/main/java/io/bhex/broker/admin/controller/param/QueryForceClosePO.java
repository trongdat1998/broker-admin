package io.bhex.broker.admin.controller.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryForceClosePO {

    private Long userId;
    private Long accountId;
    private Long fromOrderId;
    private Long endOrderId;
    private Integer limit;
}
