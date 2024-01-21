package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrokerInfoDTO {

    private Long orgId;

    private String brokerName;

    private String apiDomain;

    private String logo;

    private String favicon;

    private Map<String, Boolean> functions;

    private List<BrokerLanguageDTO> supportLanguages;

    private Boolean superior;

    private Boolean canCreateOrgApi;

    //private Map<Long, Integer> isTrust;

    private Boolean hasTrustExchange = false;
    private Boolean indexNewVersion; //首页是否是新版本

    private Boolean frontendCustomer; //前端自定义券商

    private Long dueTime;

    private Long remainTime;

    /**
     * 涨跌幅间隔： "1d","1d+8","24h"
     */
    private String realtimeInterval;

    /**
     * 过滤排行榜基础币
     */
    private Boolean filterTopBaseToken;

}
