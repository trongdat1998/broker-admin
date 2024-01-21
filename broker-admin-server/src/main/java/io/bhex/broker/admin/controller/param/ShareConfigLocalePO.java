package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 2019/7/1 11:40 AM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class ShareConfigLocalePO {

    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String downloadUrl;
    @NotNull
    @JsonDeserialize(using = LocaleInputDeserialize.class)
    private String language;
}
