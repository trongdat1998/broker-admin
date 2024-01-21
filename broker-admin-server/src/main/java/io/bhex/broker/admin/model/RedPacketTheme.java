package io.bhex.broker.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.istack.NotNull;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketTheme {

    private Long id;
    @JsonIgnore
    private Long orgId;
    @JsonIgnore
    @lombok.Builder.Default
    private String themeId = "";
    private List<Theme> themeContent;
    @JsonIgnore
    private Integer status;
    @lombok.Builder.Default
    private Integer customOrder = 0;
    @Range(max = 3, min = 1)
    private Integer type;
    private Long created;
    private Long updated;

    //    private List<Theme> themeList;
//
//    public void setThemeList(List<Theme> themeList) {
//        this.themeList = themeList;
//        this.themeContent = JsonUtil.defaultGson().toJson(themeList);
//    }
//
    @Data
    @lombok.Builder(builderClassName = "Builder", toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Theme {
        @JsonSerialize(using = LocaleOutputSerialize.class)
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        @NotNull
        private String language;
        @NotNull
        @URL
        private String backgroundUrl;
        private String backgroundColor;
        private String slogan;
    }

}
