package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 2:53 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class PageRequestPO {

    private Integer current;

    private Integer pageSize;

    private Integer platform = 0;

    private Integer bannerPosition = 1;
}
