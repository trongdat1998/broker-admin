package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class AppBaseHeaderDTO {
    private String app;
    private String appId;
    private String appVersion;
    private String nett;
    private String channel;
    private String osType;
    private String osVersion;
    private String imsi;
    private String imei;
    private String deviceToken;
}
