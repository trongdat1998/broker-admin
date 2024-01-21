package io.bhex.broker.admin.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.model
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 12:10 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Builder
@Data
public class Broker {

    private Long id;

    private Long brokerId;

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
