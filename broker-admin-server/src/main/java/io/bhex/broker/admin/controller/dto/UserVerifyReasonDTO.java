package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 24/08/2018 5:55 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class UserVerifyReasonDTO {

    private Long id;
    private String locale;
    private String reason;
}
