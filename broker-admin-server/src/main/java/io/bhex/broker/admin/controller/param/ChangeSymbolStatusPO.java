package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.SymbolValid;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 05/09/2018 10:19 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class ChangeSymbolStatusPO {

    private Integer category = 1;

//    @NotNull
//    private Long exchangeId;
    @NotNull
    @SymbolValid
    private String symbolId;

    private Boolean publishToken = false; //强制开启 未开启的币种
}
