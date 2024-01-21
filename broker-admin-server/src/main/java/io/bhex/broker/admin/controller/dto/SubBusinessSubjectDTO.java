package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubBusinessSubjectDTO {

    private Integer parentSubject;
    private Integer subject;
    private List<BusinessName> names;

    @Data
    @Builder
    public static class BusinessName {
        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String language;
        private String name;
    }

}
