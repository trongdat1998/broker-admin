package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import io.bhex.bhop.common.util.percent.Percentage;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * @Description:
 * @Date: 2018/11/6 下午3:35
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Data
public class InviteFeeBackLevelEditPO {

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long actId;

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long levelId;

    //等级条件比如某个等级需要多少人
    @PositiveOrZero
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Integer levelCondition;



    //直接返佣比例
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "0", max = "100")
    private BigDecimal directRate;

    //间接返佣比例
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "0", max = "100")
    private BigDecimal indirectRate;

}
