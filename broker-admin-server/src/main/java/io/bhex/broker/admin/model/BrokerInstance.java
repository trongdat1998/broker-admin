package io.bhex.broker.admin.model;

import lombok.Data;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.model
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 3:27 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class BrokerInstance {

    private Long id;

    private String instanceName;

    private String adminHost;

    private String httpApi;

}
