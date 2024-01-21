package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-16 09:43
 */
@Data
public class SetSpecialLoanLimitPO {

    @NotNull
    Long userId;

    @NotBlank
    String loanLimit;

    Integer isOpen = 1;
}
