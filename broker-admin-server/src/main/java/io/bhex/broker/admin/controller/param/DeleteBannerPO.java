package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 2019/7/3 6:13 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class DeleteBannerPO {

    private Long bannerId;
    private Long adminUserId;
    private Long brokerId;
}
