package io.bhex.broker.admin.controller.param;

import lombok.Data;

@Data
public class SimpleTradeDetailPO {

    private Long brokerId;

    private Long userId;

    private String baseTokenId;

    private String quoteTokenId;

    private Long orderId;

    private Long lastTradeId;

    private Long startTime;

    private Long endTime;

    private Integer limit;
}
