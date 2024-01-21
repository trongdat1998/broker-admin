package io.bhex.broker.admin.controller.param;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.base.bhadmin.TokenApplyObj;
import io.bhex.bhop.common.constant.AdminTokenTypeEnum;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import io.bhex.bhop.common.util.validation.DateStringValid;
import io.bhex.bhop.common.util.validation.UrlValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenApplyPO {

    private Long id;

    @NotNull(message = "token.record.tokenType.required")
    private Integer tokenType;

    //@NotEmpty(message = "token.record.tokenId.required")
    private String tokenId;

    @CommonInputValid
    @NotEmpty(message = "token.record.tokenName.required")
    private String tokenName;

    @CommonInputValid
    @NotEmpty(message = "token.record.tokenFullName.required")
    private String tokenFullName;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    @NotNull(message = "token.record.fairValue.required")
    private BigDecimal fairValue;

    @UrlValid
    @NotEmpty(message = "token.record.icoUrl.required")
    private String iconUrl;

    private String contractAddress;

    private String introduction;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    @NotNull(message = "token.record.minDepositingAmt.required")
    private BigDecimal minDepositingAmt;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    @NotNull(message = "token.record.minWithdrawingAmt.required")
    private BigDecimal minWithdrawingAmt;

    @CommonInputValid
    private String maxQuantitySupplied;
    private String currentTurnover;
    @UrlValid(allowEmpty = true)
    private String officialWebsiteUrl; //官网
    @UrlValid(allowEmpty = true)
    private String whitePaperUrl;

    @DateStringValid(allowEmpty = true)
    private String publishTime;

    private List<IntroductionPO> introductions = new ArrayList<>();

    private String reason;

    public TokenApplyObj toTokenRecord(Long brokerId, long exchangeId, AdminTokenTypeEnum tokenTypeEnum) {

        return TokenApplyObj.newBuilder()
            .setId(this.id == null ? 0L : this.id)
            .setExchangeId(exchangeId)
                .setBrokerId(brokerId)
            .setTokenType(this.tokenType)
            .setTokenId(this.tokenId)
            .setTokenName(this.tokenName)
                .setTokenFullName(this.tokenFullName)
            .setFairValue(DecimalUtil.fromBigDecimal(this.fairValue))
            .setIconUrl(this.iconUrl == null ? StringUtils.EMPTY : this.iconUrl)
            .setContractAddress(this.contractAddress == null ? StringUtils.EMPTY : this.contractAddress)
            .setIntroduction(JSONObject.toJSONString(introductions))
            .setMinDepositingAmt(DecimalUtil.fromBigDecimal(this.minDepositingAmt))
            .setMinWithdrawingAmt(DecimalUtil.fromBigDecimal(this.minWithdrawingAmt))
            .setReason(this.reason == null ? StringUtils.EMPTY : this.reason)
            .setFeeToken(tokenTypeEnum.name())
            .setPlatformFee(DecimalUtil.fromBigDecimal(new BigDecimal(tokenTypeEnum.getPlatformFee())))
            .setConfirmCount(tokenTypeEnum.getConfirmCount())
            .setCanWithdrawConfirmCount(tokenTypeEnum.getCanWithdrawConfirmCount())
            .setMinPrecision(tokenTypeEnum.getMinPrecision())
            .setNeedTag(tokenTypeEnum.isNeedTag() ? 1 : 0)
            .setExploreUrl(tokenTypeEnum.getExploreUrl())

            .setMaxQuantitySupplied(this.getMaxQuantitySupplied())
            .setCurrentTurnover(this.getCurrentTurnover())
            .setOfficialWebsiteUrl(this.getOfficialWebsiteUrl())
            .setWhitePaperUrl(this.getWhitePaperUrl())
            .setPublishTime(this.getPublishTime())

            .build();
    }


    @Data
    private static class IntroductionPO {
        @NotNull
        private String content;

        @NotNull
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        private String language;

        private Boolean enable;
    }

}
