package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 2:51 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class TradeFeeListPO {

    @NotNull
    private Long exchangeId;

    @NotNull
    private Integer current;

    @NotNull
    private Integer pageSize;

   // private String symbolName;

    private String baseTokenId;

    private String quoteTokenId;

    private Integer category = 1; // 默认为币币 1主类别，2创新类别, 3期权, 4期货
}
