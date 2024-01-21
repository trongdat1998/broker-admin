package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import lombok.Data;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 4:04 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class BrokerLocalConfigPO {
    private String userAgreement = "";
    private String privacyAgreement = "";
//    private String legalDescription;
//    private String helpCenter;
//
//    private String aboutUs;
//    private String aboutOpenPlatform;
//    private String aboutOrgAccount;
//    private String aboutAnnouncement;

    private String headConfig = "";

    private String footConfig = "";


    @JsonDeserialize(using = LocaleInputDeserialize.class)
    private String locale;

    private String browserTitle = "";
    private Integer enable;

    private String featureTitle = "";
    private List<BrokerFeatureConfigPO> featureConfigList;

    private String seoDescription = "";

    private String seoKeywords = "";

}
