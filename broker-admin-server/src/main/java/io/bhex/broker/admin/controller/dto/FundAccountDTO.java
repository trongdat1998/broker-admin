package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class FundAccountDTO {
    private Long id;

    private Long orgId;

    private Long userId;

    private Long accountId;

    private String tag;

    private String remark;

    private Integer isShow;

    private Long createdAt;

    private Long updatedAt;

}
