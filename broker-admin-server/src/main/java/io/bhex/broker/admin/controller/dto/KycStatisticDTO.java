package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class KycStatisticDTO {
    private Long id;
    private Long orgId;
    //统计日期 yyyy-MM-dd
    private String statisticDate;

    private Long passedKycNumber = 0L;
    private Long pcPassedKycNumber = 0L;
    private Long androidPassedKycNumber = 0L;
    private Long iosPassedKycNumber = 0L;
    private Long rejectKycNumber = 0L;
    private Long pcRejectKycNumber = 0L;
    private Long androidRejectKycNumber = 0L;
    private Long iosRejectKycNumber = 0L;
    private Long lastVerifyHistoryId = 0L;

}
