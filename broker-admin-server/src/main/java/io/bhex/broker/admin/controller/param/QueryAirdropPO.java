package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 10/11/2018 9:52 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class QueryAirdropPO {

    private Long brokerId = 0L;
    private String title = "";
    private long beginTime = 0L;
    private long endTime = 0L;
}
