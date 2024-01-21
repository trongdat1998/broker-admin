package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 05/09/2018 9:52 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class QuerySymbolsByExchangeDTO {

    public static final Integer NOT_EXIST_STATUS = 0;
    public static final Integer EXIST_STATUS = 1;

    private String symbolId;
    private String symbolName;
    private String baseTokenId;
    private String quoteTokenId;
    private String symbolAlias;
    private Integer status;
    private Long exchangeId;
}
