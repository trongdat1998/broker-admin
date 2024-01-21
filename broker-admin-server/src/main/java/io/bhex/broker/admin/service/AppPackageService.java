package io.bhex.broker.admin.service;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.broker.admin.controller.dto.AppUpdateDTO;
import io.bhex.broker.admin.controller.dto.AppVersionInfoDTO;
import io.bhex.broker.admin.controller.param.AppPackageUploadPO;
import io.bhex.broker.grpc.app_config.QueryAppUpdateLogsResponse;
import io.bhex.broker.grpc.app_config.SaveAppDownloadInfoResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @Description:
 * @Date: 2019/8/8 下午4:00
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

public interface AppPackageService {

    AppPackageUploadPO getAppDownloadInfo(long brokerId);

    SaveAppDownloadInfoResponse saveAppPackageInfo(AppPackageUploadPO po, long brokerId, AdminUserReply adminUser)  throws Exception;

    AppVersionInfoDTO getAndroidInfo(InputStream inputStream) throws IOException;

    AppVersionInfoDTO getIosInfo(InputStream inputStream) throws Exception;


    void saveAppUpdateInfo(Map<String, AppUpdateDTO> po, long brokerId);

    QueryAppUpdateLogsResponse queryAppUpdateLogs(long brokerId);
}
