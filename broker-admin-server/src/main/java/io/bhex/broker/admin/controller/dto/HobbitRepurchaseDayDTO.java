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
public class HobbitRepurchaseDayDTO {

    private Long id;

    private Long orgId;//'券商ID'

    private Long time; //时间

    private BigDecimal total;//'回购总量HBC'

    private BigDecimal averageTotal;//'平均分配总量'

    private BigDecimal performanceTotal;//'绩效分配总量'

    private BigDecimal destroyTotal;//'销毁总量'

    private BigDecimal platformTotal;//'平台运营总量'

    private Integer status;//'0未执行 1已执行 2失败'
}
