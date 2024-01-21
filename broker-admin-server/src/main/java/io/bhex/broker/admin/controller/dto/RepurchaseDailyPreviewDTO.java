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
public class RepurchaseDailyPreviewDTO {

    private Long id;

    private Long orgId;

    private String tenTimesPrice;

    private String tenTimesQuantity;

    private String fiveTimesPrice;

    private String fiveTimesQuantity;

    private String averageIncome;

    private String marketPriceTotal;
}
