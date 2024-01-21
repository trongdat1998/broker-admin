package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2018/11/14 下午5:38
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class SendVerifyCodePO {

    @NotNull
    private String username;

    // 1 手机号 2 邮箱
    private Integer userType;


    public static final int USER_TYPE_PHONE = 1;

    public static final int USER_TYPE_EMAIL = 2;
}
