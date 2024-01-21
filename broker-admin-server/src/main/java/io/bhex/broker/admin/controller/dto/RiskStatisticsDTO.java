package io.bhex.broker.admin.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RiskStatisticsDTO {

    // 用户人数
    private Integer userNum;
    //借贷市值
    private String loanValue;
    //总市值折合
    private String allValue;
    //平均风险度
    private String averageSafety;

    //有效借贷笔数
    private Integer loanOrderNum;
    //今日有效借贷笔数
    private Integer todayLoanOrderNum;
    //有效借贷人数
    private Integer loanUserNum;

}
