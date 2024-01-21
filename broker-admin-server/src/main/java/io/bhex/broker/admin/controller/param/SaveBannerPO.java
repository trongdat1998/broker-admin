package io.bhex.broker.admin.controller.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 28/08/2018 8:31 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveBannerPO {

    private Long bannerId;
    private Long adminUserId;
    private Long brokerId;
    @NotNull
    private Integer platform;
    @NotNull
    private Integer bannerPosition = 1;

    private Integer rank = 0;
    @NotNull
    private Long beginAt;
    @NotNull
    private Long endAt;

    private String remark = "";

    private List<@Valid BannerLocaleDetail> localeDetails;
}
