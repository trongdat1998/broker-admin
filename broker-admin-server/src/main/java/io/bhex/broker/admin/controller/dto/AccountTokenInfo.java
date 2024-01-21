package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class AccountTokenInfo {

    private String tokenId;
    //可借
    private String loanable;
    //已借
    private String borrowed;

}
