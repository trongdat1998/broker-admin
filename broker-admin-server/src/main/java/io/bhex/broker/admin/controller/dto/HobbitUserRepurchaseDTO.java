package io.bhex.broker.admin.controller.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HobbitUserRepurchaseDTO {

    private Long id;

    private Long orgId;//'券商ID'

    private Long time;//'时间'

    private Long userId;//'用户ID'

    private BigDecimal averageIncome;//'均分收益'

    private BigDecimal performanceIncome;//'超额收益'

    private BigDecimal inviteIncome;//'邀请收益'

    private BigDecimal inviteExpenses;//'邀请支出'

    private Integer performanceDayNum;//绩效完成天数

    private BigDecimal total;//'总收益'

    private Integer status; //0业绩不达标 1业绩达标

    private BigDecimal currentPerformance;//'总绩效'

    private BigDecimal excessPerformance;//'超额绩效'

    private BigDecimal excessPerformanceTotal;//'超额总绩效(所有队长的超额业绩累加)'

    private BigDecimal excessRate;//'超额绩效占比'

    private BigDecimal repurchaseTotal;//'30%均分+20% = repurchaseTotal'

    private BigDecimal averageTotal;//'30%均分total'
}
