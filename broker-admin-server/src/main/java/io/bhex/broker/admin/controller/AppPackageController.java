package io.bhex.broker.admin.controller;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.config.AwsPublicStorageConfig;
import io.bhex.broker.admin.controller.dto.AppUpdateDTO;
import io.bhex.broker.admin.controller.dto.AppVersionInfoDTO;
import io.bhex.broker.admin.controller.dto.BrokerInfoDTO;
import io.bhex.broker.admin.controller.param.AppPackageUploadPO;
import io.bhex.broker.admin.grpc.client.BrokerClient;
import io.bhex.broker.admin.service.AppPackageService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.common.exception.BrokerException;
import io.bhex.broker.common.objectstorage.CannedAccessControlList;
import io.bhex.broker.common.objectstorage.ObjectStorage;
import io.bhex.broker.common.objectstorage.ObjectStorageUtil;
import io.bhex.broker.common.util.FileUtil;
import io.bhex.broker.grpc.app_config.AppUpdateInfo;
import io.bhex.broker.grpc.app_config.QueryAppUpdateLogsResponse;
import io.bhex.broker.grpc.app_config.SaveAppDownloadInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date: 2019/8/8 下午3:55
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/broker/config/app_package")
public class AppPackageController  extends BaseController {

    @Autowired
    private BrokerClient brokerClient;

    @Autowired
    private AppPackageService appPackageService;


    @Resource(name = "objecPublictStorage")
    private ObjectStorage awsPublicObjectStorage;

    @Resource(name = "awsPublicStorageConfig")
    private AwsPublicStorageConfig awsPublicStorageConfig;

    @BussinessLogAnnotation(opContent = "Save App Package AndroidVersion:{#po.androidAppVersion} IosVersion:{#po.iosAppVersion}")
    @RequestMapping(value = "/save")
    public ResultModel save(@RequestBody @Valid AppPackageUploadPO po, AdminUserReply adminUser) {
        try {

            SaveAppDownloadInfoResponse response = appPackageService.saveAppPackageInfo(po, adminUser.getOrgId(), adminUser);
            if (response.getRet() == 1 || response.getRet() == 2) {
                return ResultModel.error("app.version.lower." + response.getRet());
            }

            return ResultModel.ok();
        } catch (Exception e) {
            log.error("", e);
            return ResultModel.error("");
        }
    }

    @AccessAnnotation(authIds = {1101, 1103}) //网站配置和app配置都可使用
    @RequestMapping(value = "/query")
    public ResultModel query() {
        return ResultModel.ok(appPackageService.getAppDownloadInfo(getOrgId()));
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResultModel uploadCommonImage(@RequestParam(name = "uploadFile") MultipartFile uploadImageFile,
                                         @RequestParam(value = "echoStr", required = false, defaultValue = "") String echoStr) throws Exception {
        Map<String, String> result = new HashMap<>();
        String fileType = FileUtil.getFileSuffix(uploadImageFile.getOriginalFilename(), "");
        if (Strings.isNullOrEmpty(fileType)) {
            throw new BrokerException(BrokerErrorCode.UNSUPPORTED_FILE_TYPE);
        }
        AppVersionInfoDTO dto;

        String appPrefix;
        if (fileType.equalsIgnoreCase("ipa")) {
            appPrefix = "app/ios/";
            dto = appPackageService.getIosInfo(uploadImageFile.getInputStream());
        } else if (fileType.equalsIgnoreCase("apk")) {
            appPrefix = "app/android/";
            dto = appPackageService.getAndroidInfo(uploadImageFile.getInputStream());
        } else {
            throw new BrokerException(BrokerErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        BrokerInfoDTO brokerInfoDTO = brokerClient.queryBrokerInfoById(getOrgId());
        String apiDomain = brokerInfoDTO.getApiDomain().split(",")[0].replace(".", "_");
        if (apiDomain.startsWith("_")) {
            apiDomain = apiDomain.substring(1);
        }

        //String brokerName = orgInstanceConfig.getBrokerInstance(getOrgId()).getBrokerName();

        String suffix = fileType.toLowerCase();
        String fileKey;
        if (fileType.equalsIgnoreCase("apk")) {
            fileKey = appPrefix + apiDomain + "_" + dto.getAppVersion() + "_" + new Random().nextInt(10000) + ".apk";
        } else {
            fileKey = appPrefix + ObjectStorageUtil.sha256FileName(uploadImageFile.getBytes(), suffix);
        }


        try {
            awsPublicObjectStorage.uploadObjectWithCacheControl(fileKey, ObjectStorageUtil.getFileContentType(suffix, MediaType.ANY_TYPE),
                    uploadImageFile.getInputStream(), CannedAccessControlList.PublicRead, "max-age=31536000");
        } catch (Exception e) {
            log.warn("aws upload {} error.", fileKey, e);
            return ResultModel.error("aws.upload.error");
        }

        String packageUrl = awsPublicStorageConfig.getStaticUrl() + fileKey;
        result.put("url", packageUrl);
        log.info("org:{} packageUrl:{}", getOrgId(), packageUrl);


        if (fileType.equalsIgnoreCase("ipa")) {
            String plistContent = PLIST_TMPL.replace("#ipa_url#", packageUrl)
                    .replace("#big_icon#", "")
                    .replace("#small_icon#", "")
                    .replace("#appid#", dto.getAppId())
                    .replace("#version#", dto.getAppVersion())
                    .replace("#broker_name#", Strings.nullToEmpty(dto.getIosAppName()));


            String plistFileKey = appPrefix + apiDomain + "_" + dto.getAppVersion() + "_" + System.currentTimeMillis() +  ".plist";
            try {
                awsPublicObjectStorage.uploadObject(plistFileKey, MediaType.PLAIN_TEXT_UTF_8,
                        new ByteArrayInputStream(plistContent.getBytes(Charset.forName("UTF-8"))), CannedAccessControlList.PublicRead);
            } catch (Exception e) {
                log.warn("aws upload {} error.", plistFileKey, e);
                return ResultModel.error("aws.upload.error");
            }
            String plistUrl = awsPublicStorageConfig.getStaticUrl() + plistFileKey;

            log.info("org:{} plistUrl:{} realUrl:{}", getOrgId(), plistUrl, plistUrl);
            result.put("url", "itms-services://?action=download-manifest&url=itms-services://?action=download-manifest&url=" + plistUrl);
        }


        result.put("appId",  dto.getAppId());
        result.put("appVersion", dto.getAppVersion());
        result.put("echoStr", echoStr);
        return ResultModel.ok(result);
    }


    @RequestMapping(value = "/query_update_list")
    public ResultModel queryUpdateList(AdminUserReply adminUser) {
        Map<String, Object> result = new HashMap<>();
        QueryAppUpdateLogsResponse response = appPackageService.queryAppUpdateLogs(adminUser.getOrgId());
        List<AppUpdateInfo> list = response.getAppUpdateLogList();
        if (!CollectionUtils.isEmpty(list)) {
            Map<String, List<AppUpdateInfo>> infosMap = list.stream().collect(Collectors.groupingBy(AppUpdateInfo::getDeviceType));
            for (String deviceType : infosMap.keySet()) {
                AppUpdateDTO dto = new AppUpdateDTO();
                List<AppUpdateDTO.AppUpdateItem> items = Lists.newArrayList();
                for (AppUpdateInfo info : infosMap.get(deviceType)) {
                    AppUpdateDTO.AppUpdateItem item = new AppUpdateDTO.AppUpdateItem();
                    item.setUpdateVersion(info.getUpdateVersion());
                    item.setMinVersion(info.getMinVersion());
                    item.setMaxVersion(info.getMaxVersion());
                    item.setUpdateType(info.getUpdateTypeValue());
                    item.setAppChannel(info.getAppChannel());
                    List<AppUpdateInfo.NewFeature> features = info.getNewFeatureList();
                    List<AppUpdateDTO.AppUpdateNewFeatureDTO> featureDTOS = new ArrayList<>();
                    for (AppUpdateInfo.NewFeature feature : features) {
                        AppUpdateDTO.AppUpdateNewFeatureDTO featureDTO = new AppUpdateDTO.AppUpdateNewFeatureDTO();
                        BeanCopyUtils.copyPropertiesIgnoreNull(feature, featureDTO);
                        featureDTOS.add(featureDTO);
                    }
                    item.setNewFeatures(featureDTOS);
                    items.add(item);
                }
                dto.setItems(items);
                result.put(deviceType, dto);
            }
        }

        AppPackageUploadPO packageUploadPO = appPackageService.getAppDownloadInfo(adminUser.getOrgId());

        if (packageUploadPO != null) {
            result.put("iosAppVersion", packageUploadPO.getIosAppVersion());
            result.put("androidAppVersion", packageUploadPO.getAndroidAppVersion());
        }

        result.put("androidVersions", response.getAndroidVersionsList());
        result.put("iosVersions", response.getIosVersionsList());
        //result.put("list", resultList);


        return ResultModel.ok(result);
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/save_update_info")
    public ResultModel saveAppUpgradeInfo(@RequestBody @Valid Map<String, @Valid AppUpdateDTO> po, AdminUserReply adminUser) {
        log.info("AppUpdateDTO:{}", po);
        appPackageService.saveAppUpdateInfo(po, adminUser.getOrgId());
        return ResultModel.ok();
    }


    public static final String PLIST_TMPL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
            + "<plist version=\"1.0\">\n" +
            "<dict>\n" +
            "\t<key>items</key>\n" +
            "\t<array>\n" +
            "\t\t<dict>\n" +
            "\t\t\t<key>assets</key>\n" +
            "\t\t\t<array>\n" +
            "\t\t\t\t<dict>\n" +
            "\t\t\t\t\t<key>kind</key>\n" +
            "\t\t\t\t\t<string>software-package</string>\n" +
            "\t\t\t\t\t<key>url</key>\n" +
            "\t\t\t\t\t<string>#ipa_url#</string>\n" +
            "\t\t\t\t</dict>\n" +
            "\t\t\t\t<dict>\n" +
            "\t\t\t\t\t<key>kind</key>\n" +
            "\t\t\t\t\t<string>full-size-image</string>\n" +
            "\t\t\t\t\t<key>needs-shine</key>\n" +
            "\t\t\t\t\t<true/>\n" +
            "\t\t\t\t\t<key>url</key>\n" +
            "\t\t\t\t\t<string>#big_icon#</string>\n" +
            "\t\t\t\t</dict>\n" +
            "\t\t\t\t<dict>\n" +
            "\t\t\t\t\t<key>kind</key>\n" +
            "\t\t\t\t\t<string>display-image</string>\n" +
            "\t\t\t\t\t<key>needs-shine</key>\n" +
            "\t\t\t\t\t<string>YES</string>\n" +
            "\t\t\t\t\t<key>url</key>\n" +
            "\t\t\t\t\t<string>#small_icon#</string>\n" +
            "\t\t\t\t</dict>\n" +
            "\t\t\t</array>\n" +
            "\t\t\t<key>metadata</key>\n" +
            "\t\t\t<dict>\n" +
            "\t\t\t\t<key>bundle-identifier</key>\n" +
            "\t\t\t\t<string>#appid#</string>\n" +
            "\t\t\t\t<key>bundle-version</key>\n" +
            "\t\t\t\t<string>#version#</string>\n" +
            "\t\t\t\t<key>kind</key>\n" +
            "\t\t\t\t<string>software</string>\n" +
            "\t\t\t\t<key>title</key>\n" +
            "\t\t\t\t<string>#broker_name#</string>\n" +
            "\t\t\t</dict>\n" +
            "\t\t</dict>\n" +
            "\t</array>\n" +
            "</dict>\n" +
            "</plist>\n";


}
