package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Date: 2020/1/8 下午1:08
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class TokenHoldInfoDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long balanceId;

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal total;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String tokenId;
}
