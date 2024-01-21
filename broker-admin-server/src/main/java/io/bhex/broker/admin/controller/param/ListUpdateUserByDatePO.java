package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.bhop.common.dto.param
 * @Author: ming.xu
 * @CreateDate: 2019/5/24 4:40 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class ListUpdateUserByDatePO {

    private Long brokerId;

    private Long startTime;

    private Long endTime;

    private Long fromId;

    private Long endId;

    private Integer limit;
}