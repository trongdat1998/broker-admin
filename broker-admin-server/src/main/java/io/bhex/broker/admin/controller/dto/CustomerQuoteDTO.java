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
 * @Description: 自定义交易区
 * @Date: 2020/2/17 下午2:10
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerQuoteDTO {

    private List<Item> items;

    @Data
    public static class Item {

        private String id; // base_config中的id

        private List<ContentData> contentlist;

    }

    @Data
    public static class ContentData {

        @JsonDeserialize(using = LocaleInputDeserialize.class)
        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String locale;

        private Boolean enable;

        private String tabName;

    }

}
