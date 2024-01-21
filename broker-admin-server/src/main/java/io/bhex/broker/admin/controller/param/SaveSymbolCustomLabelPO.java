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
public class SaveSymbolCustomLabelPO {

    Long orgId;

    Long labelId;

    @NotEmpty
    String colorCode;

    List<SaveCustomLabelPO.LocaleDetail> localeDetail;

    Integer type;
}
