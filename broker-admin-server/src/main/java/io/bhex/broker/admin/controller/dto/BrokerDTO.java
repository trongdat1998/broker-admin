package io.bhex.broker.admin.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 11:24 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class BrokerDTO {

    @NotNull(message = "name not null")
    private String name;

    @NotNull(message = "fullName not null")
    private String fullName;

    private String email;

    private String phone;

    private String host;

    private String contact;

    private String basicInfo;

    private BigDecimal saasFee;
}
