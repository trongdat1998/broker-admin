package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 13/09/2018 9:19 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementLocaleDetailDTO {

    private String title;
    private String content;
    @JsonSerialize(using = LocaleOutputSerialize.class)
    private String locale;
    private Integer type;
    private String pageUrl;
}
