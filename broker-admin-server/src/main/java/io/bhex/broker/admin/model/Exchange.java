package io.bhex.broker.admin.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.model
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 3:24 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Builder
@Data
public class Exchange {

    private Long id;

    private Long exchangeId;

    private Long instanceId;

    private String name;

    private String fullName;

    private String email;

    private String phone;

    private String host;

    private String earnestAddress;

    private String contact;

    private String basicInfo;

    private Boolean isBind;

    private Boolean enabled;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}
