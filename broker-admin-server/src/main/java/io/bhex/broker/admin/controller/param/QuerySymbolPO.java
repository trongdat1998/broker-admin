package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.SymbolValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 2:51 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class QuerySymbolPO {

    @NotNull
    private Integer current;
    @NotNull
    @Max(value = 1000)
    private Integer pageSize;

    private String quoteToken;
    
    private String symbolName;

    private Integer category = 1; // 默认为币币 1主类别，2创新类别, 3期权, 4期货

    private List<ExtraRequestPO> extraRequestInfos;

    private String customerQuoteId;

}
