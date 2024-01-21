package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonConfigDTO {

    private String key;
    private String desc;
    private String value;

}
