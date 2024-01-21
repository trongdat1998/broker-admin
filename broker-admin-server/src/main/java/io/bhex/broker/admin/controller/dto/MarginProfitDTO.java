package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-11 11:23
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class MarginProfitDTO {
    private Long id;

    private Long joinDate;

    private Long orgId;

    private Long userId;

    private Long accountId;

    private Long submitTime;

    private Integer kycLevel;

    private String profitRate;

    private String allPositionUsdt;

    private String todayPositionUsdt;

    private Integer joinStatus;

    private Integer dayRanking;

    private Long updates;

    private Long created;

    private Long updated;
}
