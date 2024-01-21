package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Date: 2019/1/14 下午12:06
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class InviteDailyTaskRes {

    private Long  statisticsTime;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal totalAmount;

    private Integer changeStatus;

    private Integer grantStatus;

    private Integer cashTransferStatus;
}
