package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ProjectName: exchange
 * @Package: io.bhex.ex.admin.dto
 * @Author: ming.xu
 * @CreateDate: 16/11/2018 10:25 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
public class ExCommissionFeeDTO {

    Long exchangeId;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    BigDecimal commissionRate;
}
