package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Data
public class OTCBrokerCurrencyPO {

    /**
     * 券商ID
     */
    private Long orgId;
    /**
     * 法币代码
     */
    private String code;
    /**
     * 法币名称
     */
    //private String name;
    /**
     * 多语言
     */
    //private String language;
    /**
     * 最小计价单位
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
     * 法币成交额精度
     */
    private Integer amountScale;

    //是否配置
    private Boolean configed;

    //启用=1,禁用=0
    private Integer status;

    public Integer getAmountScale(){
        if(Objects.isNull(this.amountScale)|| this.amountScale.intValue()<1){
            return getScale();
        }

        return this.amountScale;
    }

    public Integer getScale(){
        if(Objects.isNull(this.scale)|| this.scale.intValue()<1){
            return 2;
        }

        return this.scale;
    }


    public String getCode(){
        if(StringUtils.isBlank(this.code)){
            throw new IllegalArgumentException("tokenId is empty");
        }

        return this.code;
    }


/*    @Data
    public static class OTCBrokerCurrencyListPO{
        private List<OTCBrokerCurrencyPO> list;
    }*/
}
