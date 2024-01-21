package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-01-06 18:47
 */
@Data
public class DeleteAccountLoanLimitPO {
    @NotNull
    public Long userId;
}
