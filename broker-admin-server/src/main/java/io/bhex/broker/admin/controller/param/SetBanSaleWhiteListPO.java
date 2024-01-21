package io.bhex.broker.admin.controller.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2018/11/14 下午5:20
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class SetBanSaleWhiteListPO {

    @NotNull
    private String username;

    //@NotNull
    //private String symbolId;

    // 1 手机号 2 邮箱
    @NotNull
    private Integer userType;

    //private Long exchangeId;

    public static final int USER_TYPE_PHONE = 1;

    public static final int USER_TYPE_EMAIL = 2;


//    @NotNull
//    @Length(min = 0, max = 10)
//    private String verifyCode;

}