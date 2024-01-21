package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.Data;

import java.util.Map;

@Data
public class InviteCommonSettingDTO {

    @JsonSerialize(using = LocaleOutputSerialize.class)
    private String locale;

    private boolean enable;

    private Map<String, Object> settings;

//    @Data
//    public static class InviteCommonSetting {
//        private String key;
//        private String value;
//        private String desc;
//    }
}
