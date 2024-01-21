package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountFeeUserDTO {

    private Long id;

    private Long orgId;

    private Long baseGroupId;

    private Long temporaryGroupId;

    private String baseGroupName;

    private String temporaryGroupName;

    private Long useId;

    private Integer status;

    private Date created;

    private Date updated;
}
