package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import io.bhex.bhop.common.util.percent.Percentage;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import io.bhex.bhop.common.util.validation.StringInValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Data
public class UserLevelConfigPO {

    private Long levelConfigId;

    @NotEmpty
    private String levelIcon;

    @NotEmpty
    List<@Valid LocaleDetail> localeDetail;

    @Data
    public static class LocaleDetail {
        @NotEmpty
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        @JsonSerialize(using = LocaleOutputSerialize.class)
        String language;

        @CommonInputValid
        @NotEmpty
        String levelName;
    }

    @Data
    public static class Condition {

        @StringInValid({"bindMobile", "kycLevel", "balanceAmount", "7dBalanceAmount", "30dBalanceAmount",
                "spotUserFee", "contractUserFee", "30dSpotTradeAmountBtc", "30dContractTradeAmountBtc"})
        private String key;

        @Min(0)
        private BigDecimal minValue = BigDecimal.ZERO;

        @Min(0)
        private BigDecimal maxValue = BigDecimal.ZERO;

        private Integer value;

        @TokenValid(allowEmpty = true)
        private String tokenId;
    }

    @NotEmpty
    private List<List<@Valid Condition>> conditions;

//    //权益设置
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="10", max="100")
    private BigDecimal spotBuyMakerDiscount = BigDecimal.ONE;

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="10", max="100")
    private BigDecimal spotBuyTakerDiscount = BigDecimal.ONE;
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="10", max="100")
    private BigDecimal spotSellMakerDiscount = BigDecimal.ONE;
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="10", max="100")
    private BigDecimal spotSellTakerDiscount = BigDecimal.ONE;

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage
    private BigDecimal optionBuyMakerDiscount = BigDecimal.ONE;
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage
    private BigDecimal optionBuyTakerDiscount = BigDecimal.ONE;
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage
    private BigDecimal optionSellMakerDiscount = BigDecimal.ONE;
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage
    private BigDecimal optionSellTakerDiscount = BigDecimal.ONE;


    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="0", max="100")
    private BigDecimal contractBuyMakerDiscount = BigDecimal.ONE;
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="10", max="100")
    private BigDecimal contractBuyTakerDiscount = BigDecimal.ONE;
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="0", max="100")
    private BigDecimal contractSellMakerDiscount = BigDecimal.ONE;
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="10", max="100")
    private BigDecimal contractSellTakerDiscount = BigDecimal.ONE;

    private BigDecimal withdrawUpperLimitInBTC;

    private Boolean cancelOtc24hWithdrawLimit;

    private Boolean inviteBonusStatus;

    private Integer status;

    private Boolean isBaseLevel;

    List<tokenInterest> tokenInterests = new ArrayList<>();

    @Data
    public static class tokenInterest{

        private String tokenId = "" ;
        private String interest = "0";
    }
}
