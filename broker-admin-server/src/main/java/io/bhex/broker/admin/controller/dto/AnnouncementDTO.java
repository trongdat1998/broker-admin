package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 27/08/2018 4:51 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {

    public final static Integer UNPUBLISH = 1;
    public final static Integer PUBLISH = 2;
    public final static Integer DOWN = 3;

    private Long id;
    private Long brokerId;
    private Long  adminUserId;
    private Integer status;
    private Integer platform;
    private Integer rank;
    private Long beginAt;
    private Long endAt;
    private Long createdAt;
    private Integer channel;
    private List<AnnouncementLocaleDetailDTO> localeDetails;
}
