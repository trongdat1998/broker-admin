package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Date: 2019/1/14 下午12:06
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class InviteStatisticsRecordRes {

    private Long  statisticsTime;

    private Integer status;

    private String token;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal amount;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal transferAmount;

    private Long createdAt;

    private Long updatedAt;

}
