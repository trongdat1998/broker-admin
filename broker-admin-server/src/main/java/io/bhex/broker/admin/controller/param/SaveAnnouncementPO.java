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
 * @CreateDate: 27/08/2018 5:00 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveAnnouncementPO {

    private Long id;

    private Long brokerId;

    private Long  adminUserId;

    private Integer status;

    @NotNull
    private Integer platform;

    private Integer rank = 0;

    @NotNull
    private Long beginAt;

    @NotNull
    private Long endAt;

    //创建公关 默认为币币公告 1 币币公告 2 期权公告
    @NotNull
    private Integer channel = 1;


    private List<@Valid AnnouncementLocaleDetail> localeDetails;


}
