package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class LoginLogDTO {
    //private Long id;
    private String ip;
    private String region;
    private Integer status;
    private String platform;
    private Long created;

   // {"app":"bhexApp","appId":"io.bhex.app","appVersion":"2.3.0","nett":"WIFI","channel":"official","osType":"Android","osVersion":"Android10","imsi":"","imei":"28084d467fd8749a","deviceToken":""}
    //private String appBaseHeader;

    private AppBaseHeaderDTO appHeader;

    private String userAgent;
}
