package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class UserInfoDto {

    //手机号、邮箱、GA状态、交易密码状态，KYC状态


    private String mobile;

    private String email;

    private Boolean bindGa;

    private Boolean bindTradePwd;

    private Integer verifyStatus;

    private Boolean isOpenMargin;
}
