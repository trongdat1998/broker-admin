package io.bhex.broker.admin.controller.dto;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.base.bhadmin.TokenApplyObj;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenApplyRecordDTO {
    private Long id;
    // ETH
    private Integer tokenType;
    private String tokenId;
    private String tokenName;
    private String tokenFullName;
    private Integer state;
    private BigDecimal fairValue;
    private String iconUrl;
    private String contractAddress;
    private String introduction;

    private BigDecimal minDepositingAmt;
    private BigDecimal minWithdrawingAmt;
    private BigDecimal brokerWithdrawingFee;
    private BigDecimal maxWithdrawingAmt;

    private String reason;


    private String exploreUrl;
    private String maxQuantitySupplied;
    private String currentTurnover;
    private String officialWebsiteUrl; //官网
    private String whitePaperUrl;
    private String publishTime;

    private Long createAt;
    private Long updateAt;

    public static TokenApplyRecordDTO parseTokenRecord(TokenApplyObj tokenRecord) {
        List<IntroductionDTO> theIntroductions = new ArrayList<>();
        String introduction = tokenRecord.getIntroduction();
        if (StringUtils.isNotEmpty(introduction)) {
            if (!introduction.startsWith("[") || !introduction.endsWith("]")) { //兼容原有内容，不是json数组结构的
                theIntroductions.add(new IntroductionDTO(introduction, "zh_CN", true));
            } else {
                theIntroductions = JSON.parseArray(introduction, IntroductionDTO.class);
            }
        }

        return TokenApplyRecordDTO.builder()
            .id(tokenRecord.getId())
            .tokenType(tokenRecord.getTokenType())
            .tokenId(tokenRecord.getTokenId())
            .tokenName(tokenRecord.getTokenName()).tokenFullName(tokenRecord.getTokenFullName())
            .state(tokenRecord.getState())
            .fairValue(DecimalUtil.toBigDecimal(tokenRecord.getFairValue()))
            .iconUrl(tokenRecord.getIconUrl())
            .contractAddress(tokenRecord.getContractAddress())
            .introductions(theIntroductions)
            .minDepositingAmt(DecimalUtil.toBigDecimal(tokenRecord.getMinDepositingAmt()))
            .minWithdrawingAmt(DecimalUtil.toBigDecimal(tokenRecord.getMinWithdrawingAmt()))
            .brokerWithdrawingFee(DecimalUtil.toBigDecimal(tokenRecord.getBrokerWithdrawingFee()))
            .maxWithdrawingAmt(DecimalUtil.toBigDecimal(tokenRecord.getMaxWithdrawingAmt()))
            .reason(tokenRecord.getReason())
            .createAt(tokenRecord.getCreateAt())
            .updateAt(tokenRecord.getUpdateAt())
            .maxQuantitySupplied(tokenRecord.getMaxQuantitySupplied())
            .currentTurnover(tokenRecord.getCurrentTurnover())
            .officialWebsiteUrl(tokenRecord.getOfficialWebsiteUrl())
            .whitePaperUrl(tokenRecord.getWhitePaperUrl())
            .publishTime(tokenRecord.getPublishTime())
            .exploreUrl(tokenRecord.getExploreUrl())
            .build();
    }

//    public TokenRecord toTokenRecord() {
//        return TokenRecord.newBuilder()
//            .setId(this.id)
//            .setTokenType(this.tokenType)
//            .setState(this.state)
//            .setTokenId(this.tokenId)
//            .setTokenName(this.tokenName)
//            .setFairValue(DecimalUtil.fromBigDecimal(this.fairValue))
//            .setIcoUrl(this.icoUrl)
//            .setContractAddress(this.contractAddress)
//            .setIntroduction(this.introduction)
//            .setMinDepositingAmt(DecimalUtil.fromBigDecimal(this.minDepositingAmt))
//            .setMinWithdrawingAmt(DecimalUtil.fromBigDecimal(this.minWithdrawingAmt))
//            .setBrokerWithdrawingFee(DecimalUtil.fromBigDecimal(this.brokerWithdrawingFee))
//            .setMaxWithdrawingAmt(DecimalUtil.fromBigDecimal(this.maxWithdrawingAmt))
//            .setReason(this.reason)
//            .setCreateAt(this.createAt)
//            .setUpdateAt(this.updateAt)
//            .setMaxQuantitySupplied(DecimalUtil.fromBigDecimal(this.maxQuantitySupplied))
//            .setCurrentTurnover(DecimalUtil.fromBigDecimal(this.currentTurnover))
//            .setOfficialWebsiteUrl(this.officialWebsiteUrl)
//            .setWhitePaperUrl(this.whitePaperUrl)
//            .setPublishTime(this.publishTime)
//            .build();
//    }

    private List<IntroductionDTO> introductions = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class IntroductionDTO {

        @NotNull
        private String content;

        @NotNull
        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String language;

        private Boolean enable;

    }
}
