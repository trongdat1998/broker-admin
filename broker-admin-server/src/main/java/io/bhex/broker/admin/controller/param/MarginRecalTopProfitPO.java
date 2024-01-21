package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-11 13:44
 */
@Data
public class MarginRecalTopProfitPO {

    Long joinDate = 0L;
    Integer top = 20;
}
