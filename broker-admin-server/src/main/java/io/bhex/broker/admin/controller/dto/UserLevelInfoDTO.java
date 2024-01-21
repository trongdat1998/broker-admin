package io.bhex.broker.admin.controller.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class UserLevelInfoDTO {
    private Map<Integer, String> monthTradeAmountInBtc;
    private Map<Integer, String> tradeFeeInUsdt;

    private String levelName;
}
