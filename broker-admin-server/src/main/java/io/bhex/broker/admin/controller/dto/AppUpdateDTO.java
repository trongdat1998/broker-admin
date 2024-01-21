package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import io.bhex.bhop.common.util.validation.StringInValid;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class AppUpdateDTO {

    List<AppUpdateItem> items;

    @Data
    public static class AppUpdateItem {
        @StringInValid({"official", "googleplay"})
        private String appChannel;


        private String updateVersion;

        private String minVersion;

        private String maxVersion;

        private int updateType;

        private List<AppUpdateNewFeatureDTO> newFeatures = new ArrayList<>();
    }

    @Data
    public static class AppUpdateNewFeatureDTO {

        @NotNull
        private String description;

        @NotNull
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String language;

    }
}
