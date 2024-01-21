package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-02-25 11:21
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class RptMarginTradeDTO {
    public Long id;
    public Long orgId;
    public Long createTime;
    public Long tradePeopleNum;
    public Long buyPeopleNum;
    public Long sellPeopleNum;
    public Long tradeNum;
    public Long buyTradeNum;
    public Long sellTradeNum;
    public String fee;
    public String amount;
}
