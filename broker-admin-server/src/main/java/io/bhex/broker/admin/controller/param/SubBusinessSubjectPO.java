package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SubBusinessSubjectPO {

    private Long orgId;
    @NotNull
    private Integer parentSubject;
    @NotNull
    private Integer subject;
    private List<BusinessName> names;

    @Data
    public static class BusinessName {
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String language;
        private String name;
    }

}
