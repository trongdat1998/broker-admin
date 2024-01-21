package io.bhex.broker.admin.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 4:28 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class ExchangeDTO {

    private String name;

    private String fullName;

    private String email;

    private String phone;

    private String host;

    private String contact;

    private String basicInfo;

    private BigDecimal saasFee;
}
