package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.CommonInputValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SaveExperienceFundPO {

    private Integer type; //空头类型 1免费空投 2分叉空投

    @NotEmpty
    @CommonInputValid
    private String title;
    @CommonInputValid(maxLength = 256)
    private String description = "";

    @NotEmpty
    private String userIds; //用户id字符串

    @NotEmpty
    @TokenValid
    private String tokenId; //空投币种

    @Positive
    private BigDecimal tokenAmount = new BigDecimal(0); //空投币种的数量

    private Integer redeemType = 1; //赎回类型 0-不赎回 1-到期赎回

    @NotNull
    private Long redeemTime = 0L; //赎回时间

    private Integer userCount; //发送的用户总数

    private List<String> userIdList;


    private Integer authType;


    private String verifyCode;


}
