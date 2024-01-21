package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.model
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 10:19 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymbolDTO implements Serializable {

    @NotNull
    private String symbolId;

    @NotNull
    private Long exchangeId;

    @NotNull
    private String symbolName;

    @NotNull
    private String baseTokenId;

    @NotNull
    private String quoteTokenId;

    private String symbolAlias;

    private String minTradeQuantity;

    private String minTradeAmount;

    private String minPricePrecision;

    private String basePrecision;

    private Boolean allowTrade;

    private Boolean saasAllowTradeStatus;

    private Boolean allowMargin;


    private Boolean published;

    private Boolean banSellStatus;

    private Boolean banBuyStatus;

    private Boolean showStatus;

    private Boolean filterTopStatus;

    private Map<String, String> extra;


    private String depthMerge;
    private String quotePrecision;


    private String displayTokenId;

    private Long filterTime;

    private Long labelId;

    private Boolean hideFromOpenapi;

    private Boolean forbidOpenapiTrade;

    private Map<String, Integer> tags;
    private Map<String, String> configs;
    private Boolean allowPlan;

    private Integer transferStatus;
    private String transferBrokerName;

    private Integer updatingStatus; //0-没有更新 1-上架中 2-上架失败 3-下架中 4-下架失败

    private Boolean isBaas = false;
    private Boolean isAggregate = false;
    private Boolean isTest = false;
    private Boolean isPrivate = false;
    private Long applyBrokerId = 0L;
    private Boolean isMainstream;
}
