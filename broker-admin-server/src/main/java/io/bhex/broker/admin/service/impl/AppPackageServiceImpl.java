package io.bhex.broker.admin.service.impl;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.google.common.base.Strings;
import com.google.protobuf.TextFormat;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.EditReply;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.controller.dto.AppUpdateDTO;
import io.bhex.broker.admin.controller.dto.AppVersionInfoDTO;
import io.bhex.broker.admin.controller.param.AppPackageUploadPO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.grpc.client.impl.AppPackageClient;
import io.bhex.broker.admin.service.AppPackageService;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.app_config.*;
import io.bhex.broker.grpc.common.Header;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class AppPackageServiceImpl implements AppPackageService {

    @Autowired
    private AppPackageClient appPackageClient;

    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;

    public AppPackageUploadPO getAppDownloadInfo(long brokerId) {
        Header header = Header.newBuilder().setOrgId(brokerId).build();
        AppDownloadInfos infos = appPackageClient.getAppDownloadInfos(header);
        List<AppDownloadInfo> list = infos.getAppDownloadInfoList();
        AppPackageUploadPO po = new AppPackageUploadPO();
        if (!CollectionUtils.isEmpty(list)) {
            for (AppDownloadInfo info : list) {
                if (info.getDeviceType().equalsIgnoreCase("android")) {
                    po.setAndroidAppId(info.getAppId());
                    po.setAndroidAppVersion(info.getAppVersion());
                    po.setAndroidDownloadUrl(info.getDownloadUrl());
                    po.setGooglePlayDownloadUrl(info.getGooglePlayDownloadUrl());
                } else {
                    po.setIosAppId(info.getAppId());
                    po.setIosAppVersion(info.getAppVersion());
                    po.setIosDownloadUrl(info.getDownloadUrl());
                    po.setAppStoreDownloadUrl(info.getAppStoreDownloadUrl());
                    po.setTestflightDownloadUrl(info.getTestflightDownloadUrl());
                }
            }
        }

        String testfightUrl = baseConfigService.getBrokerConfig(brokerId, BizConstant.APP_DOWNLOAD_URL_GROUP,
                BizConstant.TESTFIGHT_URL, null);
        if (StringUtils.isNotEmpty(testfightUrl)) {
            po.setTestflightDownloadUrl(testfightUrl);
        }

        String appstoreUrl = baseConfigService.getBrokerConfig(brokerId, BizConstant.APP_DOWNLOAD_URL_GROUP,
                BizConstant.APP_STORE_URL, null);
        if (StringUtils.isNotEmpty(appstoreUrl)) {
            po.setAppStoreDownloadUrl(appstoreUrl);
        }

        String googlepalyUrl = baseConfigService.getBrokerConfig(brokerId, BizConstant.APP_DOWNLOAD_URL_GROUP,
                BizConstant.GOOGLE_PLAY_URL, null);
        if (StringUtils.isNotEmpty(googlepalyUrl)) {
            po.setGooglePlayDownloadUrl(googlepalyUrl);
        }

        List<AppDownloadLocaleInfo> locales = infos.getLocaleInfoList();
        if (!CollectionUtils.isEmpty(locales)) {
            List<AppPackageUploadPO.DownloadConfigLocalePO> items = locales.stream().map(locale -> {
                AppPackageUploadPO.DownloadConfigLocalePO localePO = new AppPackageUploadPO.DownloadConfigLocalePO();
                BeanCopyUtils.copyPropertiesIgnoreNull(locale, localePO);
                po.setType(locale.getDownloadType());
                return localePO;
            }).collect(Collectors.toList());
            po.setLocaleInfo(items);
        } else {
            po.setLocaleInfo(new ArrayList<>());
            po.setType(2); //默认自定义
        }

        return po;
    }


    @Override
    public SaveAppDownloadInfoResponse saveAppPackageInfo(AppPackageUploadPO po, long brokerId, AdminUserReply adminUser) throws Exception {
        log.info("po:{}", po);
        Header header = Header.newBuilder().setOrgId(brokerId).build();
        List<AppDownloadInfo> downloadInfoList = new ArrayList<>();
        if (StringUtils.isNotEmpty(po.getAndroidAppId())) {
            AppDownloadInfo androidInfo = AppDownloadInfo.newBuilder()
                    .setAppId(po.getAndroidAppId())
                    .setAppVersion(Strings.nullToEmpty(po.getAndroidAppVersion()).trim())
                    .setDeviceType("android")
                    .setAppChannel("official")
                    .setDownloadUrl(po.getAndroidDownloadUrl())
                    .setGooglePlayDownloadUrl(po.getGooglePlayDownloadUrl())
                    .build();
            downloadInfoList.add(androidInfo);
        }

        if (StringUtils.isNotEmpty(po.getIosAppVersion())) {
            AppDownloadInfo iosInfo = AppDownloadInfo.newBuilder()
                    .setAppId(po.getIosAppId())
                    .setAppVersion(Strings.nullToEmpty(po.getIosAppVersion()).trim())
                    .setDeviceType("ios")
                    .setAppChannel("enterprise")
                    .setDownloadUrl(po.getIosDownloadUrl())
                    .setAppStoreDownloadUrl(po.getAppStoreDownloadUrl())
                    .setTestflightDownloadUrl(Strings.nullToEmpty(po.getTestflightDownloadUrl()))
                    .build();
            downloadInfoList.add(iosInfo);
        }

        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.APP_DOWNLOAD_URL_GROUP);
        configPO.setKey(BizConstant.TESTFIGHT_URL);
        configPO.setValue(Strings.nullToEmpty(po.getTestflightDownloadUrl()));
        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(null);
        configPO.setWithLanguage(false);
        configPO.setStatus(1);
        EditReply reply = baseConfigService.editConfig(brokerId, configPO, adminUser);
        log.info("add testfigh {} {}", configPO, TextFormat.shortDebugString(reply));

        configPO.setKey(BizConstant.APP_STORE_URL);
        configPO.setValue(Strings.nullToEmpty(po.getAppStoreDownloadUrl()));
        reply = baseConfigService.editConfig(brokerId, configPO, adminUser);
        log.info("add appstore {} {}", configPO, TextFormat.shortDebugString(reply));

        configPO.setKey(BizConstant.GOOGLE_PLAY_URL);
        configPO.setValue(Strings.nullToEmpty(po.getGooglePlayDownloadUrl()));
        reply = baseConfigService.editConfig(brokerId, configPO, adminUser);
        log.info("add googleplay {} {}", configPO, TextFormat.shortDebugString(reply));

        List<AppDownloadLocaleInfo> locales = new ArrayList<>();
        if (!CollectionUtils.isEmpty(po.getLocaleInfo())) {
            locales = po.getLocaleInfo().stream().map(l -> {
                AppDownloadLocaleInfo.Builder builder = AppDownloadLocaleInfo.newBuilder();
                BeanCopyUtils.copyPropertiesIgnoreNull(l, builder);
                return builder.build();
            }).collect(Collectors.toList());
        }

        AppDownloadInfoRequest request = AppDownloadInfoRequest.newBuilder()
                .setHeader(header)
                .addAllAppDownloadInfo(downloadInfoList)
                .addAllLocaleInfo(locales)
                .setDownloadTypeValue(po.getType())
                .build();

        SaveAppDownloadInfoResponse response = appPackageClient.saveAppDownloadInfo(request);
        return response;

    }

    public AppVersionInfoDTO getAndroidInfo(InputStream inputStream) throws IOException {
        byte[] getData = readInputStream(inputStream);
        File file = File.createTempFile("broker-os-", ".tmp");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }


        try (ApkFile apkFile = new ApkFile(file)) {
            ApkMeta apkMeta = apkFile.getApkMeta();
            String xml = apkFile.getManifestXml();
            log.info("xml:{}", xml);
            //		<meta-data android:name="UMENG_CHANNEL" android:value="bhex" />
            int index = xml.indexOf("UMENG_CHANNEL");
            String appChannel = "";
            if (index > -1) {
                String str2 = xml.substring(index + "UMENG_CHANNEL".length() + 1);
                int index2 = str2.indexOf("\"");
                String str3 = str2.substring(index2 + 1);
                appChannel = str3.substring(0, str3.indexOf("\""));
            }
            log.info("ios info : {} {} {}", apkMeta.getPackageName(), apkMeta.getVersionName(), appChannel);
            return new AppVersionInfoDTO(apkMeta.getPackageName(), appChannel, apkMeta.getVersionName(), null, null);
        }
    }


    public AppVersionInfoDTO getIosInfo(InputStream inputStream) throws Exception {
        //byte[] plistBytes = getInfoPlist(new ZipInputStream(inputStream));
        byte[] plistBytes = getInfoPlist(new ZipArchiveInputStream(inputStream, "UTF-8", false, true));
        NSDictionary dict = (NSDictionary) PropertyListParser.parse(plistBytes);
        String appId = dict.objectForKey("CFBundleIdentifier").toString();
        String version = dict.objectForKey("CFBundleShortVersionString").toString();
        String iosAppName = dict.objectForKey("CFBundleDisplayName").toString();
        NSObject channelObj = dict.objectForKey("channel");
        String appChannel = channelObj != null ? channelObj.toString() : "";
        log.info("ios info : {} {} {} {}", appId, version, iosAppName, appChannel);
        return new AppVersionInfoDTO(appId, appChannel, version, plistBytes, iosAppName);
    }





    private byte[] getInfoPlist(ZipArchiveInputStream ipaStream) throws IOException {
        ZipArchiveEntry zipEntry;
        while ((zipEntry = ipaStream.getNextZipEntry()) != null) {
            log.info("{}",zipEntry.getName());
            if (zipEntry.getName().endsWith(".app/Info.plist")) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                int b = ipaStream.read();
                while (b >= 0) {
                    output.write(b);
                    b = ipaStream.read();
                }
                ipaStream.close();
                return output.toByteArray();
            }
        }
        throw new RuntimeException("No Info.plist found.");
    }


    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public   byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


    @Override
    public void saveAppUpdateInfo(Map<String, AppUpdateDTO> map, long brokerId) {
        log.info("po:{}", map);
        long now = System.currentTimeMillis();
        Header header = Header.newBuilder().setOrgId(brokerId).build();
        for (String deviceType : map.keySet()) {
            if (!deviceType.equals("ios") && !deviceType.equals("android")) {
                throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
            }
            AppUpdateDTO dto = map.get(deviceType);
            if (dto == null) {
                continue;
            }
            List<AppUpdateDTO.AppUpdateItem> items = dto.getItems();
            for (AppUpdateDTO.AppUpdateItem item : items) {
                if (deviceType.equals("ios") && !Arrays.asList("enterprise", "testflight", "appstore").contains(item.getAppChannel())) {
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
                }
                if (deviceType.equals("android") && !Arrays.asList("official", "googleplay").contains(item.getAppChannel())) {
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
                }
            }
            for (AppUpdateDTO.AppUpdateItem item : items) {
                AppUpdateInfo.Builder builder = AppUpdateInfo.newBuilder();
                BeanCopyUtils.copyPropertiesIgnoreNull(item, builder);
                builder.setDeviceType(deviceType.toLowerCase());
                builder.setUpdateTypeValue(item.getUpdateType());
                for (AppUpdateDTO.AppUpdateNewFeatureDTO featureDTO : item.getNewFeatures()) {
                    AppUpdateInfo.NewFeature feature = AppUpdateInfo.NewFeature.newBuilder()
                            .setLanguage(featureDTO.getLanguage())
                            .setDescription(featureDTO.getDescription())
                            .build();
                    builder.addNewFeature(feature);
                }

                appPackageClient.saveAppUpdateInfo(SaveAppUpdateInfoRequest.newBuilder()
                        .setHeader(header)
                        .setGroupId(now)
                        .setAppUpdateInfo(builder.build())
                        .build()
                );
            }
        }

    }


    public QueryAppUpdateLogsResponse queryAppUpdateLogs(long brokerId) {
        Header header = Header.newBuilder().setOrgId(brokerId).build();
        QueryAppUpdateLogsResponse response = appPackageClient.queryAppUpdateLogs(QueryAppUpdateLogsRequest.newBuilder().setHeader(header).build());
        return response;
    }





}
