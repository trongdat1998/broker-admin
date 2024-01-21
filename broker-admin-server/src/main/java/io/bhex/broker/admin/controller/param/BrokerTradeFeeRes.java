package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.bhex.base.admin.common.BrokerTradeFeeRateReply;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import io.bhex.broker.admin.constants.BizConstant;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class BrokerTradeFeeRes {

    //private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long exchangeId;

    //private Integer securityType;

    private String symbolId;

    // private String baseTokenId;

    // private String quoteToeknId;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal makerFeeRate;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal makerRewardToTakerRate;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal takerFeeRate;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal takerRewardToMakerRate;


    public static BrokerTradeFeeRes parseFrom(BrokerTradeFeeRateReply reply){
        BrokerTradeFeeRes res = new BrokerTradeFeeRes();
        BeanUtils.copyProperties(reply, res);

        res.setMakerFeeRate(new BigDecimal(reply.getMakerFeeRate()));
        res.setMakerRewardToTakerRate(new BigDecimal(reply.getMakerRewardToTakerRate()));
        res.setTakerFeeRate(new BigDecimal(reply.getTakerFeeRate()));
        res.setTakerRewardToMakerRate(new BigDecimal(reply.getTakerRewardToMakerRate()));

        return res;
    }

    public static BrokerTradeFeeRes defaultInstance(String symbolId){
        BrokerTradeFeeRes res = new BrokerTradeFeeRes();
        res.setSymbolId(symbolId);
        res.setMakerFeeRate(BizConstant.DEFAULT_TRADE_FEE_RATE
                .setScale(8,BigDecimal.ROUND_DOWN));
        res.setMakerRewardToTakerRate(BigDecimal.ZERO);
        res.setTakerFeeRate(BizConstant.DEFAULT_TRADE_FEE_RATE
                .setScale(8,BigDecimal.ROUND_DOWN));
        res.setTakerRewardToMakerRate(BigDecimal.ZERO);
        return res;
    }
}
