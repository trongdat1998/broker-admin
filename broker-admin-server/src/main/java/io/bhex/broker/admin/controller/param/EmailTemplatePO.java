package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class EmailTemplatePO {

    private List<EmailTemplate> list;

    @Data
    @Valid
    public static class EmailTemplate {

        @JsonSerialize(using = LocaleOutputSerialize.class)
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        private String language;

        @CommonInputValid(maxLength = 20480)
        private String emailTemplate;

        private Boolean enabled;
    }

}
