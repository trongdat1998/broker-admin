package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 30/11/2018 3:52 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class AutoAirdropDTO {

    private Long id;
    private Long brokerId;
    private String tokenId;
    private Integer airdropType;
    private Integer accountType;
    private Boolean status;
    private String airdropTokenNum;
    private Long updatedAt;
    private Long createdAt;
}
