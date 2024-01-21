package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 2019/12/12 9:00 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class QueryCustomLabelPO {

    private Long orgId;

    @NotNull
    private Long fromId = 0l;
    @NotNull
    private Long endId = 0L;
    private Integer limit = 20;
    private Integer type;

}
