package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import lombok.Data;

import java.util.Map;

/**
 * @Description:
 * @Date: 2019/1/24 上午10:30
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class InviteCommonSettingPO {

    @JsonDeserialize(using = LocaleInputDeserialize.class)
    private String locale;

    private Map<String, Object> settings;

}
