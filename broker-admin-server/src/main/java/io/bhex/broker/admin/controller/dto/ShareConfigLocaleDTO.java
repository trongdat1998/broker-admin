package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 2019/7/1 11:32 AM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class ShareConfigLocaleDTO {

    private String title;
    private String description;
    private String downloadUrl;
    @JsonSerialize(using = LocaleOutputSerialize.class)
    private String language;
}
