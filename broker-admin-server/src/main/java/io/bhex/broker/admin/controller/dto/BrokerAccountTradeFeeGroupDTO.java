package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Description:
 * @Date: 2018/11/21 下午5:38
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class BrokerAccountTradeFeeGroupDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;//序号

    private String groupName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long accountCount;

    //maker折扣比例
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal makerFeeRateAdjust;

    //taker折扣比例
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal takerFeeRateAdjust;

    //maker奖励分成
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal takerRewardToMakerRateAdjust;

    private Boolean status;//状态

    private Long createdAt;//用户注册时间

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accountIds;


   // private Long updatedAt;//用户注册时间
}
