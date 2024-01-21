package io.bhex.broker.admin.controller.param;

import lombok.Data;

@Data
public class QueryBrokerAgentUserPO {

    private Long userId;

    private String email;

    private String mobile;

    private String agentName;

    private Integer page;

    private Integer limit;

    private Integer fromId;

    private Integer endId;

    private String tokenId;

    private String startTime;

    private String endTime;
}
