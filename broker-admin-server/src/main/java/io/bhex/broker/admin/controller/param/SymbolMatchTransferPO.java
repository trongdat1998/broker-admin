package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.SymbolValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SymbolMatchTransferPO {

    //private Long matchBrokerId;

    @NotEmpty
    private String matchBrokerName;

    @SymbolValid
    private String symbolId;

    private Integer category;

    //private Integer takerPayForwardFeeRate;
    //private Integer makerPayForwardFeeRate;



}
