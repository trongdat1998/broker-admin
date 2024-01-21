package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 2:44 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class CreateAdminUserPO {

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
