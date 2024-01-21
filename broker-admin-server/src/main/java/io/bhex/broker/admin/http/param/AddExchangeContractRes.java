package io.bhex.broker.admin.http.param;

import lombok.Builder;
import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.http.param
 * @Author: ming.xu
 * @CreateDate: 03/09/2018 8:18 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
public class AddExchangeContractRes {

    private Long applicationId;
    private Long exchangeId;
    private Long brokerId;
    private Long adminUserId;
    private String brokerName;
    private String remark;
    private String email;
    private String contact;
}
