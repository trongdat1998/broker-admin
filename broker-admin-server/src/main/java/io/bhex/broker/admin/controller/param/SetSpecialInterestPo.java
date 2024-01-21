package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-02 09:39
 */
@Data
public class SetSpecialInterestPo {
    @NotNull
    String tokenId;
    @NotNull
    String interest;
    @NotNull
    Long userId;

    Integer effectiveFlag = 1;
}
