package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 2019/5/26 5:43 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class LockBalancePO {

    private Long brokerId;
    private String tokenId;
    private String amount;
    private Long accountId;
}
