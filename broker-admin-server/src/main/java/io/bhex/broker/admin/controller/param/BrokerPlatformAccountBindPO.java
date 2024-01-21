package io.bhex.broker.admin.controller.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @Description:将brokerName下的username绑定到targetOrgId的accountType类型账户下面
 * @Date: 2018/10/8 下午7:19
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Data
public class BrokerPlatformAccountBindPO {
    @NotNull
    private Integer accountType;

    @NotNull
    @Length(min = 1, max=99)
    private String username;//c端用户

    @NotNull
    private String brokerName;//c端用户的brokerName

    private String validateCode;
}
