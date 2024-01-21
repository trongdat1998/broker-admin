package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import io.bhex.bhop.common.util.validation.UrlValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 13/09/2018 3:33 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerLocaleDetail {
    @NotNull
    private String imageUrl;

    private String h5ImageUrl;

    @NotNull
    @JsonDeserialize(using = LocaleInputDeserialize.class)
    private String locale;

    @NotNull
    @CommonInputValid(maxLength = 512)
    private String title;

    private String content = "";

    private Integer type;

    @UrlValid(allowEmpty = true)
    private String pageUrl = "";
}
