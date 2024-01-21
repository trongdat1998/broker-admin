package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import io.bhex.bhop.common.util.validation.UrlValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 13/09/2018 10:56 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementLocaleDetail {
    @NotEmpty
    @CommonInputValid(maxLength = 512)
    private String title;

    @NotNull
    @JsonDeserialize(using = LocaleInputDeserialize.class)
    private String locale;

    private Integer type;

    @UrlValid(allowEmpty = true)
    private String pageUrl = "";
}
