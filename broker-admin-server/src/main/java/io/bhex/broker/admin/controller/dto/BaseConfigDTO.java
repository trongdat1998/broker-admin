package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class BaseConfigDTO {

    private Long id;

    private String group;

    private String key;

    private String value;

    private String extraValue;

    private String language;

    private String adminUserName;

    private String symbol;

    private String token;

    private Long created;

    private Long updated;

    private Long startTime;

    private Long endTime;

    private String newValue;

    private String newExtraValue;

    private Boolean isOpen;

}
