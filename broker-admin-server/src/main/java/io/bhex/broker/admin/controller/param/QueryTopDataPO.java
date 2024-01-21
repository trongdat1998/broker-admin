package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.DateStringValid;
import io.bhex.bhop.common.util.validation.SymbolValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class QueryTopDataPO {

    @Max(value = 500)
    private Integer pageSize = 100;

    @TokenValid(allowEmpty = true)
    private String tokenId;

    @SymbolValid(allowEmpty = true)
    private String symbolId = "";


    @DateStringValid(allowEmpty = true)
    private String date;

    @DateStringValid(allowEmpty = true)
    private String startDate;

    @DateStringValid(allowEmpty = true)
    private String endDate;

    private Integer lastIndex = 0;

    //交易类型,币币（1）、合约（3）、期权（2）、法币（100）
    private Integer tradeType = 0;

}
