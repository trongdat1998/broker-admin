package io.bhex.broker.admin.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyRiskStatisticsDTO {

    private Long date;
    //平均风险度
    private String averageSafety;
    //借贷市值(BTC)
    private String loanValue;
    //借贷市值(USDT)
    private String usdtLoanValue;
    //总市值折合(BTC)
    private String allValue;
    //总市值折合(USDT)
    private String usdtAllValue;
    // 用户人数
    private Integer userNum;
    //有效借贷人数
    private Integer loanUserNum;
    //有效借贷笔数
    private Integer loanOrderNum;
    //当日有效借贷笔数
    private Integer todayLoanOrderNum;
    //当日还款笔数
    private Integer todayPayNum;
    //当日借贷人数
    private Integer todayLoanUserNum;

}
