package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-11 13:38
 */
@Data
public class SetMarginProfitRankingPO {
    @NotNull
    Long joinDate ;
    @NotNull
    Long userId ;
    @NotNull
    Integer ranking;
}
