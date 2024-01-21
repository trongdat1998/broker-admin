package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class QueryHobbitUserKpiPO {

    private Long userId;

    private long lastId = 0L;

    private long startTime = 0;
    private long endTime = 0;

    @Max(200)
    private int pageSize = 100;

    private int type = 0; //0-kpi 1-commision

    private int queryType = 0; //0-查user自己的明细 1-查user贡献的明细

}
