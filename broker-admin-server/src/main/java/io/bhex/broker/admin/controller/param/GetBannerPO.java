package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 10/09/2018 4:18 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class GetBannerPO {

    @NotNull
    private Long bannerId;
}
