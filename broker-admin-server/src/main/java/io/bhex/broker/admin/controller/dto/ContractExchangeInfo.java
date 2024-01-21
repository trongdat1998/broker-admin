package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 21/09/2018 4:39 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class ContractExchangeInfo {

    private Long exchangeId;
    private String exchangeName;
    private String remark;
}
