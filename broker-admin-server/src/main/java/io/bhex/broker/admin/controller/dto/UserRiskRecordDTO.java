package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class UserRiskRecordDTO {

    private Long id;
    private Long userId;
    private String mobile;
    private String email;
    private String remark;
    private Long created;

}
