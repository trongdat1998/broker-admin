package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * @Description:
 * @Date: 2019/8/9 上午10:40
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
@AllArgsConstructor
public class AppVersionInfoDTO {

    private String appId;

    private String appChannel;

    private String appVersion;

    private byte[] plistBytes;

    private String iosAppName;


}
