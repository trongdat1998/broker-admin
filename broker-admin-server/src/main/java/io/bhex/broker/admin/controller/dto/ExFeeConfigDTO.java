package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @ProjectName: exchange
 * @Package: io.bhex.ex.admin.dto
 * @Author: ming.xu
 * @CreateDate: 16/11/2018 11:12 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
public class ExFeeConfigDTO {

    private ExCommissionFeeDTO exCommissionFee;
    private BrokerTradeMinFeeDTO brokerTradeMinFee;
}
