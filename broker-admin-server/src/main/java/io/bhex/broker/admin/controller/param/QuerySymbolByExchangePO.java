package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 05/09/2018 10:10 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class QuerySymbolByExchangePO {

    @NotNull
    private Integer pageSize;
    @NotNull
    private Integer current;
    @NotNull
    private Long exchangeId;
    @NotNull
    private Integer category = 1; // 默认为币币 1主类别，2创新类别, 3期权, 4期货
}
