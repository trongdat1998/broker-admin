package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class UserActionRecordDTO {

    private Long id;
    private String actionType;
   // private String action;
    private String remoteIp;
    //private Integer byAdmin; // 0 or 1
    //private String adminUser; // byAdmin = 0, 0; byAdmin = 1, adminUser;
    private String platform;
  //  private String userAgent;
  //  private String language;
   // private String appBaseHeader;
    private Integer resultCode;
    private Long created;

}
