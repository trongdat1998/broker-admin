package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-17 17:09
 */
@Data
public class DelSpecialLoanLimitPO {
    @NotNull
    Long userId;
}
