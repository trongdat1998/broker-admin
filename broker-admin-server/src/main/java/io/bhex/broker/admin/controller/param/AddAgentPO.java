package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddAgentPO {

    private Long userId;

    private Long targetUserId;

    private String agentName;

    private String leader;

    private String mobile;

    private String mark;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal coinRate;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal lowLevelCoinRate;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal contractRate;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal lowLevelContractRate;
}
