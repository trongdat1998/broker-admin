package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import io.bhex.broker.grpc.staking.StakingProductRebateCalcWay;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class StakingProductPO implements Serializable {

    private final static String ZERO_STR = "0";
    /**
     * 产品id
     */
    private Long id;

    /**
     * 券商机构ID
     */
    private Long orgId;
    /**
     * 币种ID
     */
    @TokenValid
    private String tokenId;

    /**
     * 利息计算方式，0=利率(默认) 1=金额分摊
     */
    private Integer rebateCalcWay = StakingProductRebateCalcWay.STPD_REBATE_CALCWAY_RATE_VALUE;

    /**
     * 派息方式:0=分期付息 1=一次性还本付息
     */
    @NotNull
    private Integer dividendType;

    /**
     * 派息方式值:如果是一次性付本还息,则为 1,如果是分期付息,到期付本,则这里保存的是分几期
     */
    @NotNull
    private Integer dividendTimes;

    /**
     * 发行期限:一般为 7/14/30/60/90
     */
    @NotNull
    private Integer timeLimit;

    /**
     * 只限活期使用，表示默认的年化利率
     */
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal actualApr;

    /**
     * 参考年化收益率,可能是一个范围值
     */
    private String referenceApr;

    /**
     * 每用户最低申购手
     */
    @NotNull
    private Integer perUsrLowLots;

    /**
     * 每用户最大申购手
     */
    @NotNull
    private Integer perUsrUpLots;

    /**
     * 发行最大手
     */
    @NotNull
    private Integer upLimitLots;

    /**
     * 展示发行最大手
     */
    @NotNull
    private Integer showUpLimitLots;

    /**
     * 每手金额
     */
    @BigDecimalStringValid
    private String perLotAmount;

    /**
     * 申购开始日期
     */
    @NotNull
    private Long subscribeStartDate;

    /**
     * 申购结束日期
     */
    @NotNull
    private Long subscribeEndDate;

    /**
     * 计息开始日期
     */
    @NotNull
    private Long interestStartDate;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 类型:1=定期 2=定期锁仓 3=活期
     */
    @NotNull
    private Integer type;

    /**
     * 本金账户
     */
    private Long principalAccountId;

    /**
     * 派息账户ID
     */
    @NotNull
    private Long dividendAccountId;

    /**
     * 推荐显示位置
     */
    private Integer arrposid;

    /**
     * 多语言项目信息
     */
    private List<LocalInfo> localInfos;

    /**
     * 派息配置记录
     */
    private List<Rebate> rebates;

    /**
     * 认购限制条件
     */
    private SubscribeLimit limit;

    public Long getId() {
        if (id != null) {
            return id;
        }
        return 0L;
    }

    public Long getOrgId() {
        if (orgId != null) {
            return orgId;
        }
        return 0L;
    }

    public String getTokenId() {
        if (tokenId != null) {
            return tokenId;
        }
        return "";
    }

    public Integer getDividendType() {
        if (dividendType != null) {
            return dividendType;
        }
        return -1;
    }

    public Integer getDividendTimes() {
        if (dividendTimes != null) {
            return dividendTimes;
        }
        return 0;
    }

    public Integer getTimeLimit() {
        if (timeLimit != null) {
            return timeLimit;
        }
        return 0;
    }

    public String getReferenceApr() {
        if (referenceApr != null) {
            return referenceApr;
        }
        return "";
    }

    public Integer getPerUsrLowLots() {
        if (perUsrLowLots != null) {
            return perUsrLowLots;
        }
        return 0;
    }

    public Integer getPerUsrUpLots() {
        if (perUsrUpLots != null) {
            return perUsrUpLots;
        }
        return 0;
    }

    public Integer getUpLimitLots() {
        if (upLimitLots != null) {
            return upLimitLots;
        }
        return 0;
    }

    public String getPerLotAmount() {
        if (perLotAmount != null) {
            return perLotAmount;
        }
        return "0";
    }

    public Long getSubscribeStartDate() {
        if (subscribeStartDate != null) {
            return subscribeStartDate;
        }
        return 0L;
    }

    public Long getSubscribeEndDate() {
        if (subscribeEndDate != null) {
            return subscribeEndDate;
        }
        return 0L;
    }

    public Long getInterestStartDate() {
        if (interestStartDate != null) {
            return interestStartDate;
        }
        return 0L;
    }

    public Integer getSort() {
        if (sort != null) {
            return sort;
        }
        return 0;
    }

    public Integer getType() {
        if (type != null) {
            return type;
        }
        return -1;
    }

    public Long getPrincipalAccountId() {
        if (principalAccountId != null) {
            return principalAccountId;
        }
        return 0L;
    }

    public Long getDividendAccountId() {
        if (dividendAccountId != null) {
            return dividendAccountId;
        }
        return 0L;
    }

    public Integer getArrposid() {
        if (arrposid != null) {
            return arrposid;
        }
        return 0;
    }

    public List<LocalInfo> getLocalInfos() {
        if (localInfos != null) {
            return localInfos;
        }
        return Lists.newArrayList();
    }

    public List<Rebate> getRebates() {
        if (rebates != null) {
            return rebates;
        }
        return Lists.newArrayList();
    }

    public SubscribeLimit getLimit() {
        if (limit != null) {
            return limit;
        }
        return new SubscribeLimit();
    }

    public Integer getShowUpLimitLots() {

        if (showUpLimitLots != null) {
            return showUpLimitLots;
        }
        return 0;
    }

    @Data
    public static class LocalInfo implements Serializable {

        @JsonDeserialize(using = LocaleInputDeserialize.class)
        private String language;
        /**
         * 项目名称
         */
        private String productName;
        /**
         * 协议地址
         */
        private String protocolUrl;

        /**
         * 背景图片地址
         */
        private String backgroundUrl;

        /**
         * 项目描述
         */
        private String productDetails;

        public String getLanguage() {
            if (StringUtils.isNotBlank(language)) {
                return language;
            }
            return "";
        }

        public String getProductName() {
            if (StringUtils.isNotBlank(productName)) {
                return productName;
            }
            return "";
        }

        public String getProtocolUrl() {
            if (StringUtils.isNotBlank(protocolUrl)) {
                return protocolUrl;
            }
            return "";
        }

        public String getBackgroundUrl() {
            if (StringUtils.isNotBlank(backgroundUrl)) {
                return backgroundUrl;
            }
            return "";
        }

        public String getProductDetails() {
            if (StringUtils.isNotBlank(productDetails)) {
                return productDetails;
            }
            return "";
        }
    }

    @Data
    public static class Rebate implements Serializable {
        private Long id;

        /**
         * 派息币种
         */
        private String tokenId;

        /**
         * 派息日期
         */
        private Long rebateDate;

        /**
         * 派息金额
         */
        private String rebateAmount;

        /**
         * 派息利率
         */
        @JsonDeserialize(using = PercentageInputDeserialize.class)
        private BigDecimal rebateRate;

        public Long getId() {
            if (id != null) {
                return id;
            }
            return 0L;
        }

        public Long getRebateDate() {
            if (rebateDate != null) {
                return rebateDate;
            }
            return 0L;
        }

        public BigDecimal getRebateRate() {
            if (rebateRate != null) {
                return rebateRate;
            }
            return BigDecimal.ZERO;
        }

        public String getTokenId() {
            if (tokenId == null) {
                return "";
            }
            return tokenId;
        }

        public String getRebateAmount() {
            if (rebateAmount == null) {
                return "0";
            }
            return rebateAmount;
        }

    }

    @Data
    public static class SubscribeLimit {

        /**
         * 是否校验kyc 0不校验 1校验
         */
        private Boolean verifyKyc;
        /**
         * 是否校验绑定手机
         */
        private Boolean verifyBindPhone;

        /**
         * 是否校验持仓资产 0不校验 1校验
         */
        private Boolean verifyBalance;

        /**
         * 持仓token数量
         */
        private String quantity;

        /**
         * 持仓tokenId
         */
        private String positionToken;

        /**
         * 是否校验平均持仓
         */
        private Boolean verifyAvgBalance;

        /**
         * 平均持仓开始时间
         */
        private Long verifyAvgBalanceStartTime;
        /**
         * 平均持仓结束时间
         */
        private Long verifyAvgBalanceEndTime;

        /**
         * 平均持仓持仓量条件
         */
        private String verifyAvgBalanceVolume;
        /**
         * 平均持仓持仓币种
         */
        private String verifyAvgBalanceToken;

        /**
         * VIP等级门槛
         */
        private String levelLimit;

        public String getLevelLimit() {
            if (levelLimit != null) {
                return levelLimit;
            }
            return "";
        }

        public String getQuantity() {
            if (StringUtils.isBlank(quantity)) {
                return "0";
            }
            return quantity;
        }

        public String getPositionToken() {
            if (StringUtils.isBlank(positionToken)) {
                return "";
            }
            return positionToken;
        }

        public Boolean getVerifyKyc() {
            if (verifyKyc == null) {
                return false;
            }
            return verifyKyc;
        }

        public Boolean getVerifyBindPhone() {
            if (verifyBindPhone == null) {
                return false;
            }
            return verifyBindPhone;
        }

        public Boolean getVerifyBalance() {
            if (verifyBalance == null) {
                return false;
            }
            return verifyBalance;
        }

        public Boolean getVerifyAvgBalance() {
            if (verifyAvgBalance == null) {
                return false;
            }
            return verifyAvgBalance;
        }

        public Long getVerifyAvgBalanceStartTime() {

            if (verifyAvgBalanceStartTime == null) {
                return 0L;
            }

            return verifyAvgBalanceStartTime;
        }

        public Long getVerifyAvgBalanceEndTime() {
            if (verifyAvgBalanceEndTime == null) {
                return 0L;
            }
            return verifyAvgBalanceEndTime;
        }

        public String getVerifyAvgBalanceVolume() {
            if (StringUtils.isBlank(verifyAvgBalanceVolume)) {
                return "0";
            }
            return verifyAvgBalanceVolume;
        }

        public String getVerifyAvgBalanceToken() {
            if (StringUtils.isBlank(verifyAvgBalanceVolume)) {
                return "";
            }
            return verifyAvgBalanceToken;
        }

    }
}


