package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Date: 2020/2/10 下午7:07
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexCustomerConfigDTO {
    private List<Item> items;

    @Data
    public static class Item {

        private String moduleName;

        private Boolean open;

        private Integer status; //1-直接保存 2-预览

        private List<ContentData> contentlist;
    }

    @Data
    public static class ContentData {

        @JsonDeserialize(using = LocaleInputDeserialize.class)
        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String locale;

        private String content;

        private Boolean enable;

        private String tabName;

        private Integer type;

        private List<Integer> platform;

        private Integer useModule = 0;
    }
}
