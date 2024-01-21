package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * @Description:
 * @Date: 2018/9/30 下午5:34
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Data
public class BrokerKycConfigDTO {
    private Long brokerId;
    private Long countryId;
    private Integer kycLevel;
    private String webankAppId;
    private String webankAppSecret;
    private String webankAndroidLicense;
    private String webankIosLicense;
    private String appName;
    private String companyName;
}
