package io.bhex.broker.admin.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRiskDTO {

    private Long accountId;
    //总资产
    private String total;
    //已借资产
    private String borrowed;
    //风险度
    private String safety;
    private Long userId;

}
