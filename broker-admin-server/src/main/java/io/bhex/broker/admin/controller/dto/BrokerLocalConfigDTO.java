package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 3:59 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokerLocalConfigDTO {
    private String userAgreement;
    private String privacyAgreement;
//    private String legalDescription;
//    private String helpCenter;
//    private String aboutUs;
//    private String aboutOpenPlatform;
//    private String aboutOrgAccount;
//    private String aboutAnnouncement;

    private String featureTitle;



    @JsonSerialize(using = LocaleOutputSerialize.class)
    private String locale;
    private String browserTitle;
    private Integer enable;
    private List<BrokerFeatureConfigDTO> featureConfigList;

    private String seoDescription;

    private String seoKeywords;
}
