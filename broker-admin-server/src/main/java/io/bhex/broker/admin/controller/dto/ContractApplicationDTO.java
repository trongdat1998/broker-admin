package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 31/08/2018 11:52 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class ContractApplicationDTO {

    private Long id;
    private Long brokerId;
    private Long exchangeId;
    private String exchangeName;
    private Integer status;
    private Long createdAt;
}
