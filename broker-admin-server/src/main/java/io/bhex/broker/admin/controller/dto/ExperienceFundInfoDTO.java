package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExperienceFundInfoDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //private Integer type; //类型 1合约体验金

    private String title;

    private String description;


    private String tokenId; //空投币种

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal tokenAmount; //空投币种的数量

    private Integer redeemType; //赎回类型 0-不赎回 1-到期赎回

    private Long redeemTime; //赎回时间

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal redeemAmount;

    private Long userCount; //发送的用户总数

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal transferAssetAmount; //发送token总数

    private Integer status; //状态 0.初始化，未执行 1.赠送完毕 2.赠送失败  3.赎回成功 终态

    private String failedReason; //空投失败说明

    private String adminUserName;

    private Long createdAt;

    private Long updatedAt;

    private String userIds;
}
