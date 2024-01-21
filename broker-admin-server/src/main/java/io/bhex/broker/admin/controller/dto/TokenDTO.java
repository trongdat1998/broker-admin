package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.model
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 10:18 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO implements Serializable {

    //币种Id
    @NotNull
    private String tokenId;

    //币种名称
    @NotNull
    private String tokenName;

    //币种全名
    @NotNull
    private String tokenFullName;

    private String description;

    @NotNull
    private Integer minPrecision;

    private Boolean isPublished;

    //USDT 为虚拟币，USDTBTC不是虚拟币
    private int isVirtual;

    private String depositMinQuantity;

    private Boolean allowWithdraw;

    private Boolean allowDeposit;

    private String tokenIcon;

    private String tokenDetail;

    private String withdrawToken;

    //private Map<String, String> extra;

    private Boolean isHighRiskToken;

    private String fee;

    private Boolean isBaas = false;
    private Boolean isAggregate = false;
    private Boolean isTest = false;

    private Map<String, Integer> tags;
    private Map<String, String> configs;
    private Boolean owner = false;
}
