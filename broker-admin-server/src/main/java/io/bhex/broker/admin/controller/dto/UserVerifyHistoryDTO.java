package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 24/08/2018 5:56 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class UserVerifyHistoryDTO {

    private Long id;
    private Long userVerifyId;
    private Integer verifyStatus;
    private Long adminUserId;
    private String adminUserName;
    private Long verifyReasonId;
    private String reason;
    private String remark;
    private Long infoUploadTime;
    private Long created;
    private Long updated;
}
