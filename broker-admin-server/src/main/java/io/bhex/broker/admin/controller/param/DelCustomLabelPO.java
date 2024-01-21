package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 2019/12/12 8:58 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class DelCustomLabelPO {

    Long orgId;
    @NotNull
    Long labelId;
}
