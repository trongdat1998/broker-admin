package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-01-06 18:41
 */
@Data
public class SetAccountLoanLimitPO {
    @NotNull
    public Integer vipLevel;

    public String userIds = "";

}
