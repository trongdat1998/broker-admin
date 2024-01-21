package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import lombok.Data;

@Data
public class LanguageConfigPO {

    @JsonDeserialize(using = LocaleInputDeserialize.class)
    private String language;

    private String json;

    private String tab;


}
