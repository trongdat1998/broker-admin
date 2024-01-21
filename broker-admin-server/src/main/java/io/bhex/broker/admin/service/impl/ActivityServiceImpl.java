package io.bhex.broker.admin.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.write.metadata.WriteSheet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.bhex.base.account.AccountType;
import io.bhex.bhop.common.grpc.client.BhAccountClient;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.ActivityInfoDTO;
import io.bhex.broker.admin.controller.dto.ActivityProfileDTO;
import io.bhex.broker.admin.controller.dto.ActivityPurchaseInfoDTO;
import io.bhex.broker.admin.controller.dto.BrokerUserDTO;
import io.bhex.broker.admin.controller.dto.IEOUploadDTO;
import io.bhex.broker.admin.controller.dto.IEOWhiteListDTO;
import io.bhex.broker.admin.controller.dto.LockInterestOrderInfoDto;
import io.bhex.broker.admin.controller.param.IEOProjectPO;
import io.bhex.broker.admin.controller.param.IEOWhiteListPO;
import io.bhex.broker.admin.controller.param.QueryActivityOrderPO;
import io.bhex.broker.admin.grpc.client.ActivityClient;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.service.ActivityService;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.common.exception.BrokerException;
import io.bhex.broker.grpc.activity.lockInterest.ActivityOrderInfo;
import io.bhex.broker.grpc.activity.lockInterest.ActivityOrderTaskToFailRequest;
import io.bhex.broker.grpc.activity.lockInterest.CreateActivityOrderTaskRequest;
import io.bhex.broker.grpc.activity.lockInterest.CreateActivityOrderTaskResponse;
import io.bhex.broker.grpc.activity.lockInterest.ExecuteActivityOrderTaskRequest;
import io.bhex.broker.grpc.activity.lockInterest.ExecuteActivityOrderTaskResponse;
import io.bhex.broker.grpc.activity.lockInterest.ModifyActivityOrderInfoRequest;
import io.bhex.broker.grpc.activity.lockInterest.ModifyActivityOrderInfoResponse;
import io.bhex.broker.grpc.activity.lockInterest.QueryActivityProjectInfoRequest;
import io.bhex.broker.grpc.activity.lockInterest.QueryActivityProjectInfoResponse;
import io.bhex.broker.grpc.admin.ActivityCommonInfo;
import io.bhex.broker.grpc.admin.ActivityInfo;
import io.bhex.broker.grpc.admin.ActivityLocaleInfo;
import io.bhex.broker.grpc.admin.ActivityOrderProfile;
import io.bhex.broker.grpc.admin.ActivityProjectInfo;
import io.bhex.broker.grpc.admin.ActivityResult;
import io.bhex.broker.grpc.admin.ActivityType;
import io.bhex.broker.grpc.admin.AdminQueryAllActivityOrderInfoReply;
import io.bhex.broker.grpc.admin.AdminQueryAllActivityOrderInfoRequest;
import io.bhex.broker.grpc.admin.CalculateActivityRequest;
import io.bhex.broker.grpc.admin.FindActivityReply;
import io.bhex.broker.grpc.admin.FindActivityRequest;
import io.bhex.broker.grpc.admin.FindActivityResultReply;
import io.bhex.broker.grpc.admin.ListActivityOrderReply;
import io.bhex.broker.grpc.admin.ListActivityReply;
import io.bhex.broker.grpc.admin.ListActivityRequest;
import io.bhex.broker.grpc.admin.LockPeriod;
import io.bhex.broker.grpc.admin.OnlineRequest;
import io.bhex.broker.grpc.admin.ProjectStatus;
import io.bhex.broker.grpc.admin.Qualifier;
import io.bhex.broker.grpc.admin.QueryIeoWhiteListReply;
import io.bhex.broker.grpc.admin.QueryIeoWhiteListRequest;
import io.bhex.broker.grpc.admin.SaveActivityReply;
import io.bhex.broker.grpc.admin.SaveActivityRequest;
import io.bhex.broker.grpc.admin.SaveIeoWhiteListRequest;
import io.bhex.broker.grpc.proto.AdminCommonResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

    @Resource
    private ActivityClient acitivityClient;

    @Resource
    private BhAccountClient bhAccountClient;

    @Resource
    private BrokerUserClient brokerUserClient;

    @Override
    public ResultModel createIEOProject(Long brokerId, IEOProjectPO activity) {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(SimpleTimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        String date = sdf.format(Date.from(instant));
        activity.generateProjectCode(brokerId, date);

        ResultModel resultModel = checkCreateActivityParameter(activity);
        if ((!resultModel.getCode().equals(0))) {
            return resultModel;
        }

        String purchaseLimit = "0";
        long assetUserId = getAssetUserId(brokerId);
        if (StringUtils.isEmpty(activity.getId())) {
            if (activity.getTimeConfig().getPurchaseStartTime() == null || activity.getTimeConfig().getPurchaseStartTime() == 0L) {
                return ResultModel.error("ieo.parameter.start.time.empty");
            }

            if (activity.getTimeConfig().getPurchaseEndTime() == null || activity.getTimeConfig().getPurchaseEndTime() == 0L) {
                return ResultModel.error("ieo.parameter.end.time.empty");
            }
        }

        //均摊该值设置大
        ActivityType type = ActivityType.forNumber(activity.getPurchaseMode());
        if (ActivityType.EQUAL == type) {
            purchaseLimit = Long.MAX_VALUE + "";
        }

        if (ActivityType.FLASH_SALE == type) {
            purchaseLimit = "1";
            BigDecimal price = activity.getExchangeRate();
            if (Objects.nonNull(price)) {
                purchaseLimit = bigdecimalToString(new BigDecimal(activity.getActualSaleableVolumeSafe()).multiply(price));
            }
        }

        Map<String, IEOProjectPO.MultiLangInfo> multiLangMap =
                activity.getMultiLangInfo().stream().collect(Collectors.toMap(i -> i.standardFormatLanguage(), i -> i));

        //构造grpc request对象
        List<ActivityCommonInfo> commonInfoList = multiLangMap.values().stream().map(i -> {

            return ActivityCommonInfo.newBuilder()
                    .setBannerUrl(i.getUrl())
                    .setDescription(i.getDescription())
                    .setLanguage(i.standardFormatLanguage())
                    .setIntroduction(i.getIntroduction())
                    .setId(i.getCommonIdWithDefault())
                    .setAbout(StringUtils.isEmpty(i.getAbout()) ? "" : i.getAbout())
                    .setRule(StringUtils.isEmpty(i.getRule()) ? "" : i.getRule())
                    .build();
        }).collect(Collectors.toList());

        List<ActivityLocaleInfo> localeList = multiLangMap.values().stream().map(i -> {

            return ActivityLocaleInfo.newBuilder()
                    .setBrokerId(brokerId)
                    .setBannerUrl(i.getUrl())
                    .setCirculationStr(i.getShowVolume())
                    .setLanguage(i.standardFormatLanguage())
                    .setProjectCode(activity.getProjectCode())
                    .setProjectName(i.getProjectName())
                    .setTitle(i.getProjectName())
                    .setId(i.getIdWithDefault())
                    .setTitle(i.getProjectName())
                    .build();
        }).collect(Collectors.toList());

        IEOProjectPO.Qualifier qualifier = activity.getQualifier();
        Qualifier.Builder qualifierBuilder = Qualifier.newBuilder()
                .setVerifyKyc(qualifier.getVerifyKyc())
                .setVerifyMobile(qualifier.getVerifyMobile())
                .setVerifyPosition(qualifier.getVerifyPosition())
                .setId(qualifier.getIdWithDefault());

        if (qualifier.getVerifyPosition()) {
            qualifierBuilder.setPositionToken(qualifier.getPositionToken())
                    .setPositionVolume(qualifier.getQuantity());
        }


        if (qualifier.getVerifyAvgBalance() != null && qualifier.getVerifyAvgBalance()) {
            if (qualifier.getVerifyAvgBalanceEndTime() == null || qualifier.getVerifyAvgBalanceEndTime().equals(0)) {
                return ResultModel.error("ieo.parameter.verify_avg_balance.end.time.empty");
            }

            if (qualifier.getVerifyAvgBalanceStartTime() == null || qualifier.getVerifyAvgBalanceStartTime().equals(0)) {
                return ResultModel.error("ieo.parameter.verify_avg_balance.start.time.empty");
            }

            if (StringUtils.isEmpty(qualifier.getVerifyAvgBalanceToken())) {
                return ResultModel.error("ieo.parameter.verify_avg_balance.token.empty");
            }

            if (StringUtils.isEmpty(qualifier.getVerifyAvgBalanceVolume())) {
                return ResultModel.error("ieo.parameter.verify_avg_balance.volume.empty");
            }

            if (new DateTime(qualifier.getVerifyAvgBalanceStartTime()).toString("yyyy-MM-dd")
                    .equals(new DateTime(qualifier.getVerifyAvgBalanceEndTime()).toString("yyyy-MM-dd"))) {
                return ResultModel.error("ieo.parameter.verify_avg_balance.volume.empty");
            }
        }
        qualifierBuilder.setVerifyAvgBalance(qualifier.getVerifyAvgBalance() != null ? qualifier.getVerifyAvgBalance() : false);
        qualifierBuilder.setVerifyAvgBalanceStartTime(qualifier.getVerifyAvgBalanceStartTime() != null ? qualifier.getVerifyAvgBalanceStartTime() : 0L);
        qualifierBuilder.setVerifyAvgBalanceEndTime(qualifier.getVerifyAvgBalanceEndTime() != null ? qualifier.getVerifyAvgBalanceEndTime() : 0L);
        qualifierBuilder.setVerifyAvgBalanceToken(StringUtils.isNotEmpty(qualifier.getVerifyAvgBalanceToken()) ? qualifier.getVerifyAvgBalanceToken() : "");
        qualifierBuilder.setVerifyAvgBalanceVolume(StringUtils.isNotEmpty(qualifier.getVerifyAvgBalanceVolume()) ? qualifier.getVerifyAvgBalanceVolume() : "");
        qualifierBuilder.setLevelLimit(StringUtils.isNotEmpty(qualifier.getLevelLimit()) ? qualifier.getLevelLimit() : "");

        ActivityProjectInfo projectInfo = ActivityProjectInfo.newBuilder()
                .setId(activity.getIdWithDefault())
                .addAllActivityLocaleInfo(localeList)
                .setBrokerId(brokerId)
                .setProjectType(LockPeriod.UN_FIXED)
                .setStartTime(activity.getTimeConfig().getPurchaseStartTimeSafe())
                .setEndTime(activity.getTimeConfig().getPurchaseEndTimeSafe())
                .setOnlineTime(activity.getTimeConfig().getOlineTimeSafe())
                .setResultTime(activity.getTimeConfig().getReleaseResultTimeSafe())
                .setProjectCode(activity.getProjectCode())
                .setStatus(ProjectStatus.OPEN)
                .setUserLimit(activity.getMaxPurchaseVolumeSafe())
                .setMinPurchaseLimit(activity.getPurchaseUnitVolumeSafe())
                .setPurchaseTokenId(activity.getPurchaseToken())
                .setPurchaseTokenName(activity.getPurchaseTokenName())
                .setOfferingsTokenId(activity.getOfferingsToken())
                .setOfferingsTokenName(activity.getOfferingsTokenName())
                .setTotalOfferingsCirculation(activity.getNamingVolumeSafe())
                .setPlatformLimit(purchaseLimit)
                .setPurchaseableQuantity(purchaseLimit)
                .setValuationTokenQuantity(activity.getValuationTokenVolumeSafe())
                .setOfferingsTokenQuantity(activity.getOfferingsTokenVolumeSafe())
                .setIsPurchaseLimit(1)
                .setBaseProcessPercent(activity.getBaseProcessPercentSafe())
                .setDomain(StringUtils.isEmpty(activity.getDomain()) ? "" : activity.getDomain())
                .setBrowser(StringUtils.isEmpty(activity.getBrowser()) ? "" : activity.getBrowser())
                .setWhitePaper(StringUtils.isEmpty(activity.getWhitePaper()) ? "" : activity.getWhitePaper())
                .setVersion(activity.getVersion() == null ? 0 : activity.getVersion())
                .build();

        ActivityInfo activityInfo = ActivityInfo.newBuilder()
                .setBrokerId(brokerId)
                .addAllActivityCommonInfo(commonInfoList)
                .addAllActivityProjectInfo(Lists.newArrayList(projectInfo))
                .setActivityType(type)
                .setAssetUserId(assetUserId)
                .setProjectCode(activity.getProjectCode())
                .setQualifier(qualifierBuilder.build())
                .build();

        SaveActivityRequest request = SaveActivityRequest.newBuilder()
                .setActivityInfo(activityInfo)
                .build();
        SaveActivityReply reply = acitivityClient.createActivity(request);
        if (reply.getResult()) {
            return ResultModel.ok(reply.getProjectId());
        }
        return ResultModel.error("Activity status is not allowed to be modified");
    }

    private long getAssetUserId(Long brokerId) {

        Long accountId = bhAccountClient.bindRelation(brokerId, AccountType.OPERATION_ACCOUNT);
        if (Objects.isNull(accountId) || accountId.longValue() < 1) {
            log.error("Wrong accountId,brokerId={}", brokerId);
            throw new RuntimeException("Wrong accountId");
        }

        BrokerUserDTO user = brokerUserClient.getBrokerUser(brokerId, accountId, 0L, "", "", "");
        if (Objects.isNull(user)) {
            log.error("Miss user,brokerId={},accountId={}", brokerId, accountId);
            throw new RuntimeException("User isn't exist");
        }

        return user.getUserId();
    }

    @Override
    public Pair<List<ActivityProfileDTO>, Integer> listActivity(long brokerId, int pageNo, int size,
                                                                List<Integer> typesInt, String language) {

        List<ActivityType> types = typesInt.stream().map(i -> ActivityType.forNumber(i))
                .collect(Collectors.toList());

        ListActivityRequest request = ListActivityRequest.newBuilder()
                .setBrokerId(brokerId)
                .setPageNo(pageNo)
                .setSize(size)
                .addAllActivityType(types)
                .setLanguage(language)
                .build();
        ListActivityReply reply = acitivityClient.listActivity(request);
        if (reply.getCode() != 0) {
            //todo 错误处理
        }

        if (CollectionUtils.isEmpty(reply.getActivitiesList())) {
            return Pair.of(Lists.newArrayList(), 0);
        }


        List<ActivityProfileDTO> list = reply.getActivitiesList().stream().map(i -> {
            return ActivityProfileDTO.builder()
                    .activityType(i.getActivityTypeValue())
                    .projectCode(i.getProjectCode())
                    .name(i.getName())
                    .offeringsPrice(i.getOfferingsPrice())
                    .offeringsToken(i.getOfferingsToken())
                    .purchaseToken(i.getPurchaseToken())
                    .startTime(i.getStartTime())
                    .endTime(i.getEndTime())
                    .resultTime(i.getResultTime())
                    .status(i.getStatusValue())
                    .totalVolume(i.getTotalVolume())
                    .id(i.getActivityId())
                    .isShow((i.getIsShow()))
                    .build();

        }).collect(Collectors.toList());
        return org.apache.commons.lang3.tuple.Pair.of(list, reply.getTotal());
    }

    @Override
    public IEOProjectPO findActivity(Long projectId) {

        FindActivityRequest request = FindActivityRequest.newBuilder()
                .setActivityId(projectId)
                .build();

        FindActivityReply reply = acitivityClient.findActivity(request);
        if (reply.getCode() != 0) {
            //todo 错误处理
        }

        ActivityInfo activityInfo = reply.getActivity();

        ActivityProjectInfo project = activityInfo.getActivityProjectInfo(0);
        Qualifier qualifier = activityInfo.getQualifier();

        IEOProjectPO.Qualifier qualifierDto = new IEOProjectPO.Qualifier();
        qualifierDto.setId(qualifier.getId() + "");
        qualifierDto.setVerifyKyc(qualifier.getVerifyKyc());
        qualifierDto.setVerifyMobile(qualifier.getVerifyMobile());
        qualifierDto.setVerifyPosition(qualifier.getVerifyPosition());
        qualifierDto.setVerifyAvgBalance(qualifier.getVerifyAvgBalance());
        qualifierDto.setVerifyAvgBalanceStartTime(qualifier.getVerifyAvgBalanceStartTime());
        qualifierDto.setVerifyAvgBalanceEndTime(qualifier.getVerifyAvgBalanceEndTime());
        qualifierDto.setVerifyAvgBalanceToken(qualifier.getVerifyAvgBalanceToken());
        qualifierDto.setVerifyAvgBalanceVolume(qualifier.getVerifyAvgBalanceVolume());
        qualifierDto.setLevelLimit(qualifier.getLevelLimit());

        if (qualifier.getVerifyPosition()) {
            qualifierDto.setQuantity(qualifier.getPositionVolume());
            qualifierDto.setPositionToken(qualifier.getPositionToken());
        }

        IEOProjectPO.TimeConfig config = new IEOProjectPO.TimeConfig();
        config.setOnlineTime(project.getOnlineTime());
        config.setPurchaseStartTime(project.getStartTime());
        config.setPurchaseEndTime(project.getEndTime());
        config.setReleaseResultTime(project.getResultTime());

        Map<String, ActivityCommonInfo> commonMap = activityInfo.getActivityCommonInfoList()
                .stream().collect(Collectors.toMap(i -> i.getLanguage(), i -> i));

        List<IEOProjectPO.MultiLangInfo> multiLangInfoList =
                project.getActivityLocaleInfoList().stream().map(i -> {
                    ActivityCommonInfo commonInfo = commonMap.get(i.getLanguage());
                    IEOProjectPO.MultiLangInfo multiLang = new IEOProjectPO.MultiLangInfo();
                    multiLang.setDescription(commonInfo.getDescription());
                    multiLang.setIntroduction(commonInfo.getIntroduction());
                    multiLang.setUrl(i.getBannerUrl());
                    multiLang.setProjectName(i.getProjectName());
                    multiLang.setLang(frontEndFormatLanguage(i.getLanguage()));
                    multiLang.setShowVolume(i.getCirculationStr());
                    multiLang.setId(i.getId() + "");
                    multiLang.setCommonId(commonInfo.getId() + "");
                    multiLang.setAbout(commonInfo.getAbout());
                    multiLang.setRule(commonInfo.getRule());
                    return multiLang;
                }).collect(Collectors.toList());

        boolean isCustome = multiLangInfoList.stream()
                .map(i -> StringUtils.isNoneBlank(i.getShowVolume())).findFirst().get();


        IEOProjectPO projectDto = new IEOProjectPO();

        ActivityType type = activityInfo.getActivityType();
        projectDto.setId(project.getId() + "");
        projectDto.setStatus(activityInfo.getStatus());

        projectDto.setProjectCode(activityInfo.getProjectCode());
        projectDto.setMultiLangInfo(multiLangInfoList);

        projectDto.setNamingVolume(project.getTotalOfferingsCirculation());
        projectDto.setOfferingsToken(project.getOfferingsTokenId());
        projectDto.setOfferingsTokenName(project.getOfferingsTokenName());
        projectDto.setPurchaseToken(project.getPurchaseTokenId());
        projectDto.setPurchaseTokenName(project.getPurchaseTokenName());
        projectDto.setDomain(StringUtils.isEmpty(project.getDomain()) ? "" : project.getDomain());
        projectDto.setWhitePaper(StringUtils.isEmpty(project.getWhitePaper()) ? "" : project.getWhitePaper());
        projectDto.setBrowser(StringUtils.isEmpty(project.getBrowser()) ? "" : project.getBrowser());
        projectDto.setVersion(project.getVersion());

        int priceType = 2;
        if (StringUtils.isNoneBlank(project.getOfferingsTokenQuantity()) &&
                StringUtils.isNoneBlank(project.getOfferingsTokenQuantity()) &&
                !project.getValuationTokenQuantity().equals("0")) {
            projectDto.setOfferingsTokenVolume(project.getOfferingsTokenQuantity());

            String vtv = project.getValuationTokenQuantity();
            projectDto.setValuationTokenVolume(vtv);
            priceType = 1;

        }
        projectDto.setPriceType(priceType);

        if (ActivityType.FLASH_SALE == type && priceType == 1 &&
                projectDto.validOfferingTokenVolume("0") && projectDto.validValuationTokenVolume("0")) {
            BigDecimal offeringTokenAmount = new BigDecimal(project.getPurchaseableQuantity()).divide(projectDto.getExchangeRate(), 18, RoundingMode.DOWN);
            projectDto.setActualSaleableVolume(bigdecimalToString(offeringTokenAmount));
        }

        projectDto.setTimeConfig(config);
        projectDto.setPurchaseUnitVolume(project.getMinPurchaseLimit());
        projectDto.setQualifier(qualifierDto);
        projectDto.calculateMaxPurchaseUnit(project.getUserLimit());

        int volumeType = isCustome ? 1 : 2;
        projectDto.setVolumeType(volumeType);
        projectDto.setPurchaseMode(type.getNumber());
        projectDto.setBaseProcessPercent(project.getBaseProcessPercent());
        return projectDto;

    }

    @Override
    public ActivityPurchaseInfoDTO calculateActivityResult(long projectId, long brokerId, String language, String actualOfferingsVolume) {
        CalculateActivityRequest request = CalculateActivityRequest.newBuilder()
                .setBrokerId(brokerId)
                .setActualOffingsVolume(actualOfferingsVolume)
                .setLanguage(language)
                .setProjectId(projectId)
                .build();
        AdminCommonResponse reply = acitivityClient.calculateActivityResult(request);
        if (reply.getErrorCode() != 0) {
            throw new RuntimeException(reply.getMsg());
        }
        return findActivityResult(projectId, brokerId, language);
    }

    @Override
    public ActivityPurchaseInfoDTO findActivityResult(long projectId, long brokerId, String language) {
        FindActivityRequest request = FindActivityRequest.newBuilder()
                .setBrokerId(brokerId)
                .setLanguage(language)
                .setActivityId(projectId)
                .build();
        FindActivityResultReply reply = acitivityClient.findActivityResult(request);
        if (reply.getCode() != 0) {
            throw new RuntimeException(reply.getMessage());
        }

        ActivityResult result = reply.getResult();
        ActivityPurchaseInfoDTO dto = ActivityPurchaseInfoDTO.builder()
                .projectName(result.getProjectName())
                .totalPurchaseVolume(result.getTotalPurchaseVolume())
                .actualPurchaseVolume(result.getActualPurchaseVolume())
                .buyerCount(result.getBuyerCount())
                .realSoldAmount(result.getRealSoldAmount())
                .realRaiseAmount(result.getRealRaiseAmount())
                .offingsVolumeEachPurchaseUnit(result.getOffingsVolumeEachPurchaseUnit())
                .realOffingsVolumeEachPurchaseUnit(result.getRealOffingsVolumeEachPurchaseUnit())
                .build();
        return dto;
    }

    @Override
    public ResultModel confirmResult(String language, Long projectId, Long brokerId) {
        FindActivityRequest request = FindActivityRequest.newBuilder()
                .setActivityId(projectId)
                .setBrokerId(brokerId)
                .setLanguage(language)
                .build();
        AdminCommonResponse response = acitivityClient.confirmActivityResult(request);
        if (!response.getSuccess()) {
            return ResultModel.error(response.getMsg());
        }
        return ResultModel.ok();
    }

    @Override
    public boolean onlineStatus(Long projectId, Long brokerId, Integer isShow) {
        OnlineRequest request = OnlineRequest.newBuilder()
                .setProjectId(projectId)
                .setBrokerId(brokerId)
                .setOnlineStatus(isShow.equals(1) ? true : false)
                .build();
        AdminCommonResponse response = acitivityClient.onlineActivity(request);
        if (!response.getSuccess()) {
            log.warn("onlineStatus fail,activityId={},brokerId={},message={}", projectId, brokerId, response.getMsg());
        }
        return response.getSuccess();
    }


    //todo 导出订单数据, 未实现完 采用easyexcel三方库
    //@Override
    public WriteSheet exportActivityOrder(Long projectId, Long brokerId, boolean onlineStatus) {

        FindActivityRequest request = FindActivityRequest.newBuilder()
                .setActivityId(projectId)
                .setBrokerId(brokerId)
                .build();
        ListActivityOrderReply response = acitivityClient.listActivityOrder(request);
        if (response.getCode() != 0) {
            log.warn("export execl fail,activityId={},brokerId={}", projectId, brokerId);
            //
        }

        List<ActivityOrderProfile> list = response.getOrdersList();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        ActivityOrderProfile profile = list.get(0);
        //EasyExcel.
        WriteSheet sheet = new WriteSheet();
        sheet.setSheetName(profile.getProjectCode());
        //sheet

        return null;


    }


    //标准化语言为前端格式,exp.zh-cn,en-us
    public String frontEndFormatLanguage(String language) {
        String[] array = language.split("_");
        return array[0] + "-" + array[1].toLowerCase();
    }

    public String bigdecimalToString(BigDecimal bd) {
        return bd.stripTrailingZeros().toPlainString();
    }

    @Data
    public static class OrderProfileModel {

        @ExcelProperty(value = "project id", index = 0)
        private Long projectId;

        @ExcelProperty(value = "user id", index = 1)
        private Long userId;

        @ExcelProperty(value = "order id", index = 2)
        private Long orderId;

        @ExcelProperty(value = "tokenId", index = 3)
        private String tokenId;

        @ExcelProperty(value = "volume", index = 4)
        private String amount;

        @ExcelProperty(value = "status", index = 5, converter = OrderStatusConvert.class)
        private String orderStatus;
    }

    public static class OrderStatusConvert implements Converter<Integer> {

        @Override
        public Class supportJavaTypeKey() {
            return Integer.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public Integer convertToJavaData(CellData cellData, ExcelContentProperty contentProperty,
                                         GlobalConfiguration globalConfiguration) throws Exception {
            return null;
        }

        @Override
        public CellData convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                           GlobalConfiguration globalConfiguration) throws Exception {
            //支付状态 0 待支付 1 成功 2 失败
            if (0 == value) {
                return new CellData("待支付");
            }

            if (0 == value) {
                return new CellData("支付成功");
            }

            if (0 == value) {
                return new CellData("支付失败");
            }

            return new CellData("错误");
        }
    }


    @Override
    public List<LockInterestOrderInfoDto> adminQueryAllActivityOrderInfo(QueryActivityOrderPO queryActivityOrderPO) {
        if (queryActivityOrderPO.getProjectId() == null || queryActivityOrderPO.getProjectId().equals(0)) {
            log.warn("query adminQueryAllActivityOrderInfo projectId is null");
            return new ArrayList<>();
        }

        if (queryActivityOrderPO.getOrgId() == null || queryActivityOrderPO.getOrgId().equals(0)) {
            log.warn("query adminQueryAllActivityOrderInfo orgId is null");
            return new ArrayList<>();
        }

        if (queryActivityOrderPO.getLimit() == null || queryActivityOrderPO.getLimit().equals(0)) {
            queryActivityOrderPO.setLimit(20);
        }
        AdminQueryAllActivityOrderInfoRequest request = AdminQueryAllActivityOrderInfoRequest
                .newBuilder()
                .setEmail(StringUtils.isEmpty(queryActivityOrderPO.getEmail()) ? "" : queryActivityOrderPO.getEmail())
                .setMobile(StringUtils.isEmpty(queryActivityOrderPO.getMobile()) ? "" : queryActivityOrderPO.getMobile())
                .setFromId(queryActivityOrderPO.getFromId() == null ? 0 : queryActivityOrderPO.getFromId())
                .setEndId(queryActivityOrderPO.getEndId() == null ? 0 : queryActivityOrderPO.getEndId())
                .setOrgId(queryActivityOrderPO.getOrgId())
                .setProjectCode(StringUtils.isEmpty(queryActivityOrderPO.getProjectCode()) ? "" : queryActivityOrderPO.getProjectCode())
                .setProjectId(queryActivityOrderPO.getProjectId())
                .setLimit(queryActivityOrderPO.getLimit())
                .setUserId(queryActivityOrderPO.getUserId() == null ? 0 : queryActivityOrderPO.getUserId())
                .setLanguage(StringUtils.isEmpty(queryActivityOrderPO.getLanguage()) ? "zh_CN" : queryActivityOrderPO.getLanguage())
                .build();

        AdminQueryAllActivityOrderInfoReply adminQueryAllActivityOrderInfoReply
                = acitivityClient.adminQueryAllActivityOrderInfo(request);

        if (adminQueryAllActivityOrderInfoReply.getOrderInfoCount() == 0) {
            return new ArrayList<>();
        }
        List<LockInterestOrderInfoDto> lockInterestOrderInfo = new ArrayList<>();
        adminQueryAllActivityOrderInfoReply.getOrderInfoList().forEach(order -> {
            lockInterestOrderInfo.add(LockInterestOrderInfoDto
                    .builder()
                    .price(order.getPrice())
                    .projectName(order.getProjectName())
                    .purchaseTime(order.getPurchaseTime())
                    .purchaseTokenName(order.getPurchaseTokenName())
                    .amount(order.getAmount())
                    .orderId(String.valueOf(order.getOrderId()))
                    .orderQuantity(order.getOrderQuantity())
                    .userId(order.getUserId())
                    .receiveTokenId(order.getReceiveTokenId())
                    .receiveTokenName(order.getReceiveTokenName())
                    .receiveTokenQuantity(order.getReceiveTokenQuantity())
                    .mappingId(order.getMappingId())
                    .backAmount(order.getBackAmount())
                    .useAmount(order.getUseAmount())
                    .build());
        });
        return lockInterestOrderInfo;
    }

    private ResultModel checkCreateActivityParameter(IEOProjectPO ieoProjectPO) {
        if (StringUtils.isEmpty(ieoProjectPO.getOfferingsToken())) {
            return ResultModel.error("ieo.parameter.offering_token.empty");
        }

//        if (StringUtils.isEmpty(ieoProjectPO.getOfferingsTokenName())) {
//            return ResultModel.error("ieo.parameter.offering_token_name.empty");
//        }

        if (StringUtils.isEmpty(ieoProjectPO.getPurchaseToken())) {
            return ResultModel.error("ieo.parameter.purchase_token.empty");
        }
//        if (StringUtils.isEmpty(ieoProjectPO.getPurchaseTokenName())) {
//            return ResultModel.error("ieo.parameter.purchase_token_name.empty");
//        }

        if (ieoProjectPO.getPurchaseMode() == 0) {
            return ResultModel.error("ieo.parameter.purchase_mode.empty");
        }
        return ResultModel.ok();
    }


    @Override
    public ResultModel<IEOWhiteListDTO> queryIeoWhiteList(IEOWhiteListPO whiteList) {
        try {
            QueryIeoWhiteListReply reply = this.acitivityClient.queryIeoWhiteList(QueryIeoWhiteListRequest
                    .newBuilder()
                    .setBrokerId(whiteList.getBrokerId())
                    .setProjectId(whiteList.getProjectId())
                    .build());
            List<String> userList = new ArrayList<>();
            if (reply != null && StringUtils.isNotEmpty(reply.getUserIdStr())) {
                if (StringUtils.isNotEmpty(reply.getUserIdStr())) {
                    userList = Splitter.on(",").omitEmptyStrings().splitToList(reply.getUserIdStr());
                }
            }
            IEOWhiteListDTO whiteListDTO = IEOWhiteListDTO
                    .builder()
                    .brokerId(whiteList.getBrokerId())
                    .projectId(whiteList.getProjectId())
                    .userList(userList)
                    .build();
            return ResultModel.ok(whiteListDTO);
        } catch (Exception ex) {
            log.error("queryIeoWhiteList brokerId {} projectId {} error {}", whiteList.getBrokerId(), whiteList.getProjectId(), ex);
            return ResultModel.error("internal.real.error");
        }
    }

    @Override
    public ResultModel saveIeoWhiteList(IEOWhiteListPO whiteList) {
        if (whiteList.getBrokerId() == null || whiteList.getBrokerId().equals(0)) {
            return ResultModel.error("Request parameter error");
        }

        if (whiteList.getProjectId() == null || whiteList.getProjectId().equals(0)) {
            return ResultModel.error("Request parameter error");
        }
        String userStr = "";
        if (whiteList.getUserList() != null && whiteList.getUserList().size() > 0) {
            userStr = Joiner.on(",").skipNulls().join(whiteList.getUserList());
        }

        try {
            this.acitivityClient.saveIeoWhiteList(SaveIeoWhiteListRequest
                    .newBuilder()
                    .setBrokerId(whiteList.getBrokerId())
                    .setProjectId(whiteList.getProjectId())
                    .setUserIdStr(userStr)
                    .build());
            return ResultModel.ok();
        } catch (Exception ex) {
            log.error("saveIeoWhiteList brokerId {} projectId {} error {}", whiteList.getBrokerId(), whiteList.getProjectId(), ex);
            return ResultModel.error("internal.real.error");
        }
    }

    public static void main(String[] args) {
        List<Integer> numList3 = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 89, 9);
        List<List<Integer>> partList3 = Lists.partition(numList3, 3);
//        for (List<Integer> list : partList3) {
//            for (int i = 0, len = list.size(); i < len; i++) {
//                list.set(i, 8);
//            }
//        }
        System.out.println(new Gson().toJson(partList3));
    }

    @Override
    public ResultModel modifyActivityOrderInfo(Long orgId, Long projectId, String url, List<IEOUploadDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return ResultModel.error("ieo.activity.upload.data.empty");
        }
        log.info("modifyActivityOrderInfo orgId {} projectId {} url {} dtoList {} ", orgId, projectId, url, new Gson().toJson(dtoList));
        CreateActivityOrderTaskResponse response = acitivityClient.createActivityOrderTask(CreateActivityOrderTaskRequest.newBuilder()
                .setOrgId(orgId)
                .setProjectId(projectId)
                .setUrl(StringUtils.isEmpty(url) ? "" : url)
                .build());

        log.info("createActivityOrderTask orgId {} projectId {} response code {} ", orgId, projectId, response.getCode());

        if (response.getCode() == 1) {
            return ResultModel.error("ieo.activity.end.limit");
        } else if (response.getCode() == 2) {
            return ResultModel.error("ieo.activity.calculated.limit");
        } else if (response.getCode() == 3) {
            return ResultModel.error("ieo.activity.upload.data.processing");
        } else if (response.getCode() == 4) {
            return ResultModel.error("ieo.activity.failed.upload.data");
        }
        //先拿到返回的TaskID 然后把list批量的存储 最后异步执行操作 并且操作中不允许新建任务
        List<List<IEOUploadDTO>> orderList = Lists.partition(dtoList, 1000);
        try {
            orderList.forEach(info -> {
                List<ActivityOrderInfo> activityOrderInfoList = info.stream().map(order -> {
                    return ActivityOrderInfo
                            .newBuilder()
                            .setId(order.getId())
                            .setUserId(order.getUserId())
                            .setOrderId(order.getOrderId())
                            .setAmount(order.getAmount())
                            .setUseAmount(order.getUseAmount())
                            .setLuckyAmount(order.getLuckyAmount())
                            .setBackAmount(order.getBackAmount())
                            .setTaskId(response.getTaskId())
                            .setProjectId(response.getProjectId())
                            .build();
                }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(activityOrderInfoList)) {
                    //存储需要修改的order list
                    ModifyActivityOrderInfoResponse modifyResponse = acitivityClient.modifyActivityOrderInfo(ModifyActivityOrderInfoRequest
                            .newBuilder()
                            .setOrgId(orgId)
                            .setProjectId(projectId)
                            .setTaskId(response.getTaskId())
                            .addAllOrderInfo(activityOrderInfoList)
                            .build());

                    log.info("modifyActivityOrderInfo orgId {} projectId {} response code {} ", orgId, projectId, modifyResponse.getBasicRet().getCode());
                    if (modifyResponse.getBasicRet().getCode() != 0) {
                        throw new BrokerException(BrokerErrorCode.SYSTEM_ERROR);
                    }
                }
            });
        } catch (Exception ex) {
            log.info("modifyActivityOrderInfo error {} ", ex);
            //创建任务结果失败回调
            this.acitivityClient.activityOrderTaskToFail(ActivityOrderTaskToFailRequest.newBuilder()
                    .setOrgId(orgId)
                    .setProjectId(projectId)
                    .setTaskId(response.getTaskId())
                    .build());
            return ResultModel.error("ieo.activity.upload.data.timeout");
        }
        ExecuteActivityOrderTaskResponse taskResponse
                = this.acitivityClient.executeActivityOrderTask(ExecuteActivityOrderTaskRequest.newBuilder().setTaskId(response.getTaskId()).build());
        log.info("executeActivityOrderTask orgId {} projectId {}  response code {} ", orgId, projectId, taskResponse.getBasicRet().getCode());
        if (taskResponse.getBasicRet().getCode() == 0) {
            return ResultModel.ok();
        } else if (taskResponse.getBasicRet().getCode() == 1) {
            //上传超时稍后重试
            return ResultModel.error("ieo.activity.upload.data.timeout");
        } else if (taskResponse.getBasicRet().getCode() == 2) {
            //数据待计算请检查处理
            return ResultModel.error("ieo.activity.upload.data.calculated");
        } else if (taskResponse.getBasicRet().getCode() == 3) {
            //上传数据为空请检查处理
            return ResultModel.error("ieo.activity.upload.data.isnull");
        } else if (taskResponse.getBasicRet().getCode() == 4) {
            //上传数据条数与待分配条数不一致请检查处理
            return ResultModel.error("ieo.activity.upload.data.inconsistent");
        } else if (taskResponse.getBasicRet().getCode() == 5) {
            //超出锁定金额请检查处理
            return ResultModel.error("ieo.activity.upload.data.exceeding");
        } else if (taskResponse.getBasicRet().getCode() == 6) {
            //低于锁定金额请检查处理
            return ResultModel.error("ieo.activity.upload.data.below");
        } else if (taskResponse.getBasicRet().getCode() == 7) {
            //上传出现处理异常
            return ResultModel.error("ieo.activity.upload.data.exception");
        }
        return ResultModel.ok();
    }

    @Override
    public ActivityInfoDTO queryActivityProjectInfo(long orgId, long projectId) {
        QueryActivityProjectInfoResponse response = this.acitivityClient.queryActivityProjectInfo(QueryActivityProjectInfoRequest
                .newBuilder()
                .setOrgId(orgId)
                .setProejctId(projectId)
                .build());
        return ActivityInfoDTO
                .builder()
                .orgId(response != null ? response.getOrgId() : 0L)
                .projectId(response != null ? response.getProjectId() : 0L)
                .amount(response != null ? response.getAmount() : "")
                .useAmount(response != null ? response.getUseAmount() : "")
                .luckyAmount(response != null ? response.getLuckyAmount() : "")
                .backAmount(response != null ? response.getBackAmount() : "")
                .build();
    }
}
