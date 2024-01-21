package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-12-12 14:43
 */
@Data
public class UserBatchUnlockAirDropPo {
    private Long orgId;
    @NotEmpty
    private String tokenId;

    private Integer unlockType=1; //解锁用户类型，1全部 2指定user_id

    private String userIds="";

    private String mark="";
}
