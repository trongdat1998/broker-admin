package io.bhex.broker.admin.controller.param;

import com.google.common.base.Joiner;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import io.bhex.broker.common.util.StringUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.stream.Collectors;


@Data
public class IEOProjectPO implements Serializable {
    private final static String ZERO_STR="0";

    //申购模式，1=锁仓,2=分配，3=抢购
    private int purchaseMode;

    //申购币种id
    @TokenValid
    private String purchaseToken;

    private String purchaseTokenName;

    //发行币种id
    @TokenValid
    private String offeringsToken;

    private String offeringsTokenName;

    //价格类型,1=确定,2=待定
    private int priceType;

    //发行价格,申购币种数量
    @BigDecimalStringValid(allowEmpty = true)
    private String valuationTokenVolume;

    //发行价格,发行币种数量
    @BigDecimalStringValid(allowEmpty = true)
    private String offeringsTokenVolume;

    //发行总量类型,1=自定义,2=非自定义
    private int volumeType;

    //名义发行总量
    @BigDecimalStringValid(allowEmpty = true)
    private String namingVolume;

    //实际发行总量
    @BigDecimalStringValid(allowEmpty = true)
    private String actualSaleableVolume;

    //认购配置
    private TimeConfig timeConfig;

    //认购资格
    private Qualifier qualifier;

    //每份申购数量,申购币种
    @BigDecimalStringValid(allowEmpty = true)
    private String purchaseUnitVolume;

    //最大申购份数
    private int maxPurchaseUnit;

    //初始百分比
    private String baseProcessPercent;

    //多语言项目信息
    private List<MultiLangInfo> multiLangInfo;

    private String projectCode;

    private String id;

    //状态，1=预热,2=开始购买,3=购买结束,4=公布结果,5=结束
    private int status;

    private String domain; //官网

    private String whitePaper;//白皮书

    private String browser;//区块链浏览器

    private Integer version;//版本 0老版本 1新版本

    public void setTimeConfig(TimeConfig tc){
        this.timeConfig=tc;
        tc.setPurchaseMode(this.purchaseMode);
    }

    public Long getIdWithDefault(){
        if(StringUtils.isNoneBlank(this.id)){
            return Long.parseLong(this.id);
        }

        return 0L;
    }

    public void generateProjectCode(Long brokerId,String date){
        if(StringUtils.isBlank(projectCode)){
            this.projectCode= Joiner.on("-").join(this.offeringsToken,this.purchaseToken,brokerId,date);
        }
    }

    public String getNamingVolumeSafe(){
        if(StringUtils.isBlank(this.namingVolume)){
            return ZERO_STR;
        }

        return this.namingVolume;
    }

    public String getActualSaleableVolumeSafe(){
        if(StringUtils.isBlank(this.actualSaleableVolume)){
            return ZERO_STR;
        }

        return this.actualSaleableVolume;
    }

    public String getValuationTokenVolumeSafe(){
        if(StringUtils.isBlank(this.valuationTokenVolume)){
            return ZERO_STR;
        }

        return this.valuationTokenVolume;
    }

    public String getOfferingsTokenVolumeSafe(){
        if(StringUtils.isBlank(this.offeringsTokenVolume)){
            return ZERO_STR;
        }

        return this.offeringsTokenVolume;
    }

    public BigDecimal getExchangeRate(){
        try{
            if(priceType==1){
                return new BigDecimal(this.valuationTokenVolume).divide(new BigDecimal(this.offeringsTokenVolume),18,RoundingMode.DOWN);
            }
        }catch (Exception e){
            return null;
        }

        return null;
    }

    public String getPurchaseUnitVolumeSafe(){
        if(StringUtils.isBlank(this.purchaseUnitVolume)){
            return ZERO_STR;
        }

        return this.purchaseUnitVolume;
    }

    public String getMaxPurchaseVolumeSafe(){
        if(StringUtils.isBlank(this.purchaseUnitVolume)){
            return ZERO_STR;
        }

        return new BigDecimal(this.purchaseUnitVolume)
                .multiply(new BigDecimal(this.maxPurchaseUnit+""))
                .stripTrailingZeros().toPlainString();
    }

    public void calculateMaxPurchaseUnit(String maxLimit) {
        BigDecimal maxLimitBd=new BigDecimal(maxLimit);
        this.maxPurchaseUnit=maxLimitBd.divide(new BigDecimal(purchaseUnitVolume),2, RoundingMode.HALF_DOWN).intValue();
    }

    public String getBaseProcessPercentSafe() {

        if(StringUtils.isBlank(this.baseProcessPercent)){
            return ZERO_STR;
        }

        return this.baseProcessPercent;
    }

    public void validProject(){
        this.validArg();
        this.validArgLen();
    }

    public void validArg(){
        if(purchaseMode==0){
            throw new IllegalArgumentException("Purchase mode is necessary");
        }

        if(priceType==0){
            throw new IllegalArgumentException("priceType is necessary");
        }

        if(priceType==1){
            if(StringUtils.isBlank(this.valuationTokenVolume)){
                throw new IllegalArgumentException("Valuation TokenVolume is necessary");
            }

            if(StringUtils.isBlank(this.offeringsTokenVolume)){
                throw new IllegalArgumentException("Offerings TokenVolume is necessary");
            }
        }

        if(Objects.isNull(this.multiLangInfo) || this.multiLangInfo.size()==0){
            throw new IllegalArgumentException("Miss project multiLanguage");
        }

        for(MultiLangInfo mli:multiLangInfo){
            mli.validArg();
        }

        if(StringUtils.isBlank(this.purchaseToken)){
            throw new IllegalArgumentException("Miss purchase token");
        }

        if(StringUtils.isBlank(this.offeringsToken)){
            throw new IllegalArgumentException("Miss offerings token");
        }

        if(Objects.nonNull(timeConfig)){
            timeConfig.checkArg();
        }
    }

    public void validArgLen(){
        for(MultiLangInfo mli:multiLangInfo){
            mli.validArgLen();
        }
    }

    public boolean validOfferingTokenVolume(String value) {
        try{
            return new BigDecimal(this.offeringsTokenVolume).compareTo(new BigDecimal(value))>0;
        }catch (Exception e){
            return false;
        }
    }

    public boolean validValuationTokenVolume(String value) {
        try{
            return new BigDecimal(this.valuationTokenVolume).compareTo(new BigDecimal(value))>0;
        }catch (Exception e){
            return false;
        }
    }


    @Data
    public static class Qualifier implements Serializable {

        private String id;

        //是否需要kyc
        private Boolean verifyKyc;

        //是否需要绑定手机号
        private Boolean verifyMobile;

        //是否校验持仓
        private Boolean verifyPosition;

        //持仓token数量
        private String quantity;

        //持仓tokenId
        private String positionToken;

        private Boolean verifyAvgBalance;

        private Long verifyAvgBalanceStartTime;

        private Long verifyAvgBalanceEndTime;

        private String verifyAvgBalanceVolume;

        private String verifyAvgBalanceToken;

        private String levelLimit;

        public Long getIdWithDefault(){
            if(StringUtils.isNoneBlank(this.id)){
                return Long.parseLong(this.id);
            }

            return 0L;
        }
    }

    @Data
    public static class LockPosition implements Serializable {

        //是否锁仓
        private Boolean lockEnable;

        //锁仓比率
        private String percentage;

        //解锁类型,1=auto自动解锁,2=trade交易解锁
        private int unlockType;
    }

    @Data
    public static class TimeConfig implements Serializable {

        //申购模式，1=锁仓,2=分配，3=抢购
        private int purchaseMode;

        //申购开始时间,毫秒
        private Long purchaseStartTime;

        //申购结束时间,毫秒
        private Long purchaseEndTime;

        //上线时间,毫秒
        private Long onlineTime;

        //公布结果时间,毫秒
        private Long releaseResultTime;

        public Long getPurchaseStartTimeSafe(){
            if(Objects.isNull(purchaseStartTime)){
                return 0L;
            }

            return purchaseStartTime;
        }

        public Long getPurchaseEndTimeSafe(){
            if(Objects.isNull(purchaseEndTime)){
                return 0L;
            }

            return purchaseEndTime;
        }

        public Long getOlineTimeSafe(){
            if(Objects.isNull(onlineTime)){
                return 0L;
            }

            return onlineTime;
        }

        public Long getReleaseResultTimeSafe(){
            if(Objects.isNull(releaseResultTime)){
                return 0L;
            }

            return releaseResultTime;
        }

        public long getOnlineTimeSafe() {

            if(Objects.isNull(onlineTime)){
                return 0L;
            }

            return onlineTime;
        }

        public void checkArg(){
            long startTime= this.getPurchaseStartTimeSafe();
            long endTime = this.getPurchaseEndTimeSafe();
            //long onlineTime = this.getOnlineTimeSafe();
            long resultTime = this.getReleaseResultTimeSafe();

/*            if(startTime<System.currentTimeMillis()){
                throw new IllegalArgumentException("startTime must be greater than now");
            }*/

            if(endTime<startTime){
                throw new IllegalArgumentException("endTime must be greater than startTime");
            }

            //分配校验公布结果时间
            if(purchaseMode==2){
                if(resultTime<endTime && resultTime>0){
                    throw new IllegalArgumentException("resultTime must be greater than endTime");
                }
            }

/*            if(onlineTime<resultTime && onlineTime>0){
                throw new IllegalArgumentException("onlineTime must be greater than resultTime");
            }*/
        }
    }

    @Data
    public static class MultiLangInfo implements Serializable {

        private String id;

        private String lang;

        //项目名称
        private String projectName;

        //页面分配总量类型,1=自定义,2=非自定义
        private String showVolume;

        //描述
        private String introduction;

        //图片url
        private String url;

        //项目介绍
        private String description;

        //项目规则
        private String rule;

        //关于
        private String about;

        private String commonId;

        private transient String languageStandardFormat;

        public Long getIdWithDefault(){
            if(StringUtils.isNoneBlank(this.id)){
                return Long.parseLong(this.id);
            }

            return 0L;
        }

        public Long getCommonIdWithDefault(){
            if(StringUtils.isNoneBlank(this.commonId)){
                return Long.parseLong(this.commonId);
            }

            return 0L;
        }

        //标准化语言格式,exp.zh_CN,en_US
        public String standardFormatLanguage(){
            if(StringUtils.isNoneBlank(languageStandardFormat)){
                return languageStandardFormat;
            }

            String[] array=this.lang.split("-");
            this.languageStandardFormat=array[0]+"_"+array[1].toUpperCase();
            return languageStandardFormat;
        }

        public void validArgLen(){
            if(StringUtils.isNoneBlank(this.projectName)){
                if(StringUtil.getLength(projectName,"utf-8")>64){
                    throw new IllegalArgumentException("Do not exceed 64 characters for project name");
                }
            }
        }

        public void validArg(){

            if(StringUtils.isBlank(this.lang)){
                throw new IllegalArgumentException("Language is necessary");
            }

            if(StringUtils.isBlank(this.projectName)){
                throw new IllegalArgumentException("Project name is necessary in"+lang);
            }
        }

    }

/*    @Data
    public static class Description implements Serializable {
        private String title;
        private String content;
        private Integer type;//1=文字,2=图片
    }*/
}


