package io.bhex.broker.admin.controller.param;

import javax.validation.constraints.NotNull;

import io.bhex.bhop.common.util.validation.CommonInputValid;
import io.bhex.bhop.common.util.validation.IntInValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

@Data
public class GetOtcItemPO {

    private Long userId;

    private String nationalCode;

    private String phone;

    private String email;

    @NotNull
    private Integer current;

    private long lastId = 0;

    @NotNull
    private Integer pageSize;

    @TokenValid(allowEmpty = true)
    private String tokenId;

    @IntInValid({1, 2, 0}) //1-BUY 2-SELL
    private int side = 0;

    @IntInValid({10, 20, 30, 0}) //10-进行中 20-删除 30-已完成
    private int status = 0;

    @CommonInputValid
    private String nickname;
}
