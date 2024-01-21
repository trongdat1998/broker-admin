package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.percent.Percentage;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Description:
 * @Date: 2018/11/21 下午5:38
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class BrokerAccountTradeFeeGroupPO {

    //有此值就是修改，没有是新加
    private Long groupId;

    @NotEmpty
    @Length(min = 1, max = 99)
    private String groupName;

    @NotEmpty
    private String accountIds;

    //maker折扣比例
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="0", max="100")
    private BigDecimal makerFeeRateAdjust;

    //taker折扣比例
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="0", max="100")
    private BigDecimal takerFeeRateAdjust;

    //maker奖励分成
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="0", max="100")
    private BigDecimal takerRewardToMakerRateAdjust;

}
