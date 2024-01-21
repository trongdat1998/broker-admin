package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 16/08/2018 4:50 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class AdminUserDTO {

    private Long brokerId;

    private String name;

    private String fullName;

    private String email;

    private String phone;

    private String host;

    private String earnestAddress;

    private String contact;

    private String basicInfo;

}
