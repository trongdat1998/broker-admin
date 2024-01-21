package io.bhex.broker.admin.controller.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QueryBrokerAgentPO {

    private Long userId;

    private String agentName;

    private Integer page;

    private Integer limit;
}
