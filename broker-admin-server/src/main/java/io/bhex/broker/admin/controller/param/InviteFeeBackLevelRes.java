package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Date: 2018/11/6 下午3:35
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Data
public class InviteFeeBackLevelRes {
    private Long levelId;

    //等级条件比如某个等级需要多少人
    private Integer levelCondition;

    //等级标识  1-6
    private Integer level;

    //等级名称
    private String levelTag;


    //直接返佣比例
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal directRate;

    //间接返佣比例
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal indirectRate;

}
