package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 2019/12/12 8:48 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class SaveCustomLabelPO {

    Long orgId;

    Long labelId;

    @NotEmpty
    String colorCode;

    @NotEmpty
    String userIdsStr;

    List<LocaleDetail> localeDetail;

    Integer type;

    @Data
    @Valid
    public static class LocaleDetail {
        @NotEmpty
        @JsonDeserialize(using = LocaleInputDeserialize.class)
        String language;

        @CommonInputValid
        @NotEmpty
        String labelValue;
    }
}
