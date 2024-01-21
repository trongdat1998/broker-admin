package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class OrgApiKeyDTO {

    private Long id;
    private Long userId;
    private Long accountId;
    private String apiKey;
    private String secretKey;
    private String tag;
    private String ipWhiteList;
    private Integer type;
    private Integer level;
    private Integer status;
    private Long created;
    private Long updated;

}
