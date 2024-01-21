package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.Data;

import java.util.List;

@Data
public class AppIndexModuleDTO {

    private Integer moduleType;

    private List<ModuleDTO> modules;

    @Data
    public static class ModuleDTO {
        @JsonSerialize(using = LocaleOutputSerialize.class)
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        private String language;


        private List<ItemDTO> items;
    }

    @Data
    public static class ItemDTO {

        private String moduleName;

        //private String moduleIcon;

        private Integer jumpType;

        private String jumpUrl;

        private Integer loginShow;


        private String darkDefaultIcon;

        private String darkSelectedIcon;

        private String lightDefaultIcon;

        private String lightSelectedIcon;

    }





}
