package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-01-11 16:40
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class MarginPoolRptDTO {
    private Long id;
    private Long orgId;
    private String tokenId;
    // 期初数量
    private String beginAmount;
    //币池可用
    private String available;
    //未还数量
    private String unpaidAmount;
    //未还利息
    private String interestUnpaid;
    //已还利息
    private String interestPaid;
    //累计利息
    private String totalInterest;
    private Long created;
    private Long updated;
}
