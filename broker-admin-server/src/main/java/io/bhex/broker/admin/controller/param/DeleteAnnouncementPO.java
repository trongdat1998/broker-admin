package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 2019/7/4 10:21 AM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class DeleteAnnouncementPO {

    private Long announcementId;
    private Long adminUserId;
    private Long brokerId;
}
