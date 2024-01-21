package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @ProjectName:
 * @Package:
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2020-05-09 14:26
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class RepurchaseInfoDTO {

    private Long orgId;

    private String tokenId;

    private String allocated;

    private String repurchaseTotal;

    private String unsoldTotal;

    private String incomeTotal;

    private String averageIncome;

    private String tenTimesPrice;

    private String fiveTimesPrice;

    private String total;

    private String circulationTotal;

    private String lockTotal;

    private String destroyTotal;
}
