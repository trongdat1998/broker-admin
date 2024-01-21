package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class AccountDTO {

    private Long id;
    private Long orgId;
    private Long accountId;
    private String accountName;
    private Long accountType;
    private Long accountIndex;
    private Boolean authorizedOrg;
    private List<AccountTokenInfo> tokenList;

}
