package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class SubAccountDTO {

    private Integer accountType = 0;

    private Integer index = 0;

    private Long accountId = 0L;

    private String accountName = "";

    private Integer status = 0;

}

