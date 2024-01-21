package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 29/08/2018 11:24 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDTO {

    public final static Integer UNPUBLISH = 1;
    public final static Integer PUBLISH = 2;
    public final static Integer DOWN = 3;

    private Long bannerId;
    private Long adminUserId;
    private Long brokerId;
    private Integer platform;
    private Integer bannerPosition;
    private Integer rank;
    private Long beginAt;
    private Long endAt;
    private Integer status;
    private String remark;
    private Long createAt;
    private List<BannerLocaleDetailDTO> localeDetails;
}
