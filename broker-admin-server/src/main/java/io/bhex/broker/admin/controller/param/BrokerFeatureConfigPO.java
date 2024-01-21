package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 4:05 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class BrokerFeatureConfigPO {
    private String imageUrl;
    private String title;
    private String description;
    @JsonDeserialize(using = LocaleInputDeserialize.class)
    private String locale;
    private Integer index;
}
