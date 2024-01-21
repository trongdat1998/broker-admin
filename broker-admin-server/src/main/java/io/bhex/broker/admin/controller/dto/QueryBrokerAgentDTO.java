package io.bhex.broker.admin.controller.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.*;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryBrokerAgentDTO {

    private String id;

    private String userId;

    private String agentName;

    private String mobile;

    private String email;

    private Integer level;

    private String leader;

    private String leaderMobile;

    private String superiorName;

    private Integer peopleNumber;

    private String mark;

    private String time;

    private Integer status;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal lowLevelCoinRate;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal lowLevelContractRate;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal coinRate;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal contractRate;
}
