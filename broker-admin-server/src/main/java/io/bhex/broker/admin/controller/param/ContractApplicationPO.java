package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 03/09/2018 3:06 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class ContractApplicationPO {

    private Long brokerId;
    private Long exchangeId;
    private Long adminUserId;
    private String exchangeName;
    private Long applicationId;
}
