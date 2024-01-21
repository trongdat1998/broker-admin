package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 18/09/2018 6:07 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class ChangeContractApplicationPO {
    private Long applicationId;
    private Long exchangeId;
}
