package io.bhex.broker.admin.controller.dto;

import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OTCBrokerTokenDTO {

    private Long brokerId;

    /**
     * id
     */
    private String Id;
    /**
     * token币种
     */
    @TokenValid(allowEmpty = true)
    private String tokenId;

    /**
     * token名字
     */
    @TokenValid(allowEmpty = true)
    private String tokenName;
    /**
     * 最小限制额
     */
    @BigDecimalStringValid
    private String minQuote;
    /**
     * 最大限额
     */
    @BigDecimalStringValid
    private String maxQuote;
    /**
     * 精度
     */
    private Integer scale;
    /**
     * 状态  1：可用   -1,0=禁用
     */
    private Integer status;

    /**
     * 浮动范围最大值
     */
    private String upRange;

    /**
     * 浮动范围最小值
     */
    private String downRange;

    /**
     * 排序值
     */
    private Integer sequence;

    /**
     * 共享状态 1=开启共享，0=关闭共享
     */
    private Integer shareStatus;

    @BigDecimalStringValid(allowEmpty = true)
    private String feeRateOfBuy;

    @BigDecimalStringValid(allowEmpty = true)
    private String feeRateOfSell;


    public String getTokenName(){
        if(StringUtils.isBlank(this.tokenName)){
            return getTokenId();
        }

        return this.tokenName;
    }

    public String getTokenId(){
        if(StringUtils.isBlank(this.tokenId)){
            throw new IllegalArgumentException("tokenId is empty");
        }

        return this.tokenId;
    }
}
