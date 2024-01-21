package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 12:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushTaskLocaleDetail {

    private String pushSummary = "";

    @NotEmpty
    @CommonInputValid(maxLength = 120)
    private String pushTitle;

    @NotNull
    @JsonDeserialize(using = LocaleInputDeserialize.class)
    private String locale;

    @NotEmpty
    @CommonInputValid(maxLength = 512)
    private String pushContent;

    @NotEmpty
    private String  pushUrl;

    @Max(2)
    private Integer urlType;
}
