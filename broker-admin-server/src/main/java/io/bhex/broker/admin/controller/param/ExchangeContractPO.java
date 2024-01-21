package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 03/09/2018 3:09 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class ExchangeContractPO {

    private Long brokerId;
    private Long exchangeId;
    private Long contractId;
    private Long adminUserId;
    private String exchangeName = "";
    private String remark = "";
    private String email = "";
    private String contact = "";
    private String companyName = "";
    private String phone = "";
}
