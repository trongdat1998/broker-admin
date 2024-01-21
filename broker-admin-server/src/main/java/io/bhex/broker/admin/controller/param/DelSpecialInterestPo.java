package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-02 09:53
 */
@Data
public class DelSpecialInterestPo {
    @NotNull
    String tokenId;
    @NotNull
    Long userId;
}
