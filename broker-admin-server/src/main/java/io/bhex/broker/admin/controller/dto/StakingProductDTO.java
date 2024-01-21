package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class StakingProductDTO implements Serializable {

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
    private String tokenId;

    /**
     * 币种ID
     */
    private String tokenName;

    /**
     * 派息方式:1=分期付息 2=一次性还本付息
     */
    private Integer dividendType;

    /**
     * 派息方式值:如果是一次性付本还息,则为 1,如果是分期付息,到期付本,则这里保存的是分几期
     */
    private Integer dividendTimes;

    /**
     * 发行期限:一般为 7/14/30/60/90
     */
    private Integer timeLimit;

    /**
     * 参考年化收益率,可能是一个范围值
     */
    private String referenceApr;

    /**
     * 实际年化利率,定期和活动和参考年化是一样的值
     */
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal actualApr;

    /**
     * 每用户最低申购份额
     */
    private Integer perUsrLowLots;

    /**
     * 每用户最大申购份额
     */
    private Integer perUsrUpLots;

    /**
     * 发行最大份额
     */
    private Integer upLimitLots;

    /**
     * 展示发行最大份额
     */
    private Integer showUpLimitLots;

    /**
     * 已售份额
     */
    private Integer soldLots;

    /**
     * 每份额金额
     */
    private String perLotAmount;

    /**
     * 开始日期
     */
    private Long subscribeStartDate;

    /**
     * 结束日期
     */
    private Long subscribeEndDate;

    /**
     * 开始计息日期
     */
    private Long interestStartDate;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 类型:1=定期 2=定期锁仓 3=活期
     */
    private Integer type;

    /**
     * 可见状态 0下架 1上架
     */
    private Integer isShow;

    /**
     * 产品状态
     */
    private Integer status;

    /**
     * 本金账户
     */
    private Long principalAccountId;

    /**
     * 派息账户ID
     */
    private Long dividendAccountId;

    /**
     * 资金流:1=转账 2=锁仓
     */
    private Integer fundFlow;

    /**
     * 推荐显示位置
     */
    private Integer arrposid;

    private Long createdAt;

    private Long updatedAt;

    /**
     * 多语言项目信息
     */
    private List<LocalInfo> localInfos;

    /**
     * 派息配置记录
     */
    private List<Rebate> rebates;

    /**
     * 认购门槛
     */
    private SubscribeLimit limit;

    /**
     * 利息计算方式 0=年化利率 1=固定金额
     */
    private Integer rebateCalcWay;

    @Data
    public static class LocalInfo implements Serializable {

        @JsonSerialize(using = LocaleOutputSerialize.class)
        private String language;

        /**
         * 项目名称
         */
        private String productName;

        /**
         * 项目描述
         */
        private String productDetails;

        /**
         * 协议地址
         */
        private String protocolUrl;

        /**
         * 状态 0=无效 1=有效
         */
        private Boolean enabled;
    }

    @Data
    public static class Rebate implements Serializable {

        /**
         * 派息配置记录id
         */
        private Long id;

        private String tokenId;

        private String tokenName;

        /**
         * 派息日期
         */
        private Long rebateDate;

        /**
         * 派息利率
         */
        @JsonSerialize(using = PercentageOutputSerialize.class)
        private BigDecimal rebateRate;

        private String rebateAmount;

        /**
         * 是否可改 0=不可改 1=可改
         */
        private Integer canModify;
    }

    @Data
    public static class SubscribeLimit implements Serializable {
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
         * 等级门槛
         */
        private String levelLimit;
    }
}


