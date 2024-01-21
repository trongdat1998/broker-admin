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
 * @CreateDate: 10/09/2018 7:48 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerLocaleDetailDTO {

    private Long id;
    private Long adminUserId;
    private String title;
    private String content;
    private String imageUrl;
    private String h5ImageUrl;
    private Integer type;
    private String pageUrl;
    private String remark;
    @JsonSerialize(using = LocaleOutputSerialize.class)
    private String locale;
}
