package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 20/11/2018 6:06 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class DepositOrderListPO {

    private Long userId = 0L;

    private String nationalCode = "";

    private String mobile = "";

    private String email = "";

    private Long fromId = 0L;

    private Long lastId = 0L;

    private Boolean next = false;

    private Integer pageSize = 30;

    private String tokenId;

    private Long startTime = 0L;

    private Long endTime = 0L;

    private String address;

    private String txId;
}
