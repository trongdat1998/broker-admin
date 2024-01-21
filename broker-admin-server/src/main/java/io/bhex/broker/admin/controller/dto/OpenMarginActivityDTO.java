package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-08 17:00
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class OpenMarginActivityDTO {
    private Long id;

    private Long orgId;

    private Long userId;

    private Long accountId;

    private Long submitTime;

    private Integer kycLevel;

    private String allPositionUsdt;

    private String todayPositionUsdt;

    private String monthPositionUsdt;

    private Integer joinStatus;

    private Long created;

    private Long updated;

    private String lotteryNo;
}
