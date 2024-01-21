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
 * @CreateDate: 2019/12/12 9:02 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomLabelDTO {

    Long orgId;
    Long labelId;
    String colorCode;
    Integer userCount;
    String userIdsStr;
    Long createdAt;
    Long updatedAt;
    List<CustomLabelLocalDetailDTO> localeDetail;
}
