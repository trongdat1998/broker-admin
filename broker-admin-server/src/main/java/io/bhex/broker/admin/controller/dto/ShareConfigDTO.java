package io.bhex.broker.admin.controller.dto;

import lombok.Data;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 2019/7/1 11:30 AM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class ShareConfigDTO {

    private Long id;
    private Long brokerId;
    private String logoUrl;
    private String watermarkImageUrl;
    private Integer status;
    private Integer type;
    private Long adminUserId;
    private Long createdTime;
    private Long updatedTime;
    private List<ShareConfigLocaleDTO> localeInfo;
}
