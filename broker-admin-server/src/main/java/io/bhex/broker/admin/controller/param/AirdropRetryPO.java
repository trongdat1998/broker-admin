package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 12/11/2018 10:54 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class AirdropRetryPO {

    private Long airdropId;
    private Long brokerId;
}
