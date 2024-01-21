package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.bhex.bhop.common.util.DecimalOutputSerialize;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExperienceFundTransferRecordDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;



    private String tokenId; //空投币种

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal tokenAmount; //空投币种的数量



    private Long redeemTime; //赎回时间

    @JsonSerialize(using = DecimalOutputSerialize.class)
    private BigDecimal redeemAmount;



    private Integer status; //状态 0.初始化，未执行 1.赠送完毕 2.赠送失败  3.赎回成功 终态



    private Long userId;

    private Long accountId;
}
