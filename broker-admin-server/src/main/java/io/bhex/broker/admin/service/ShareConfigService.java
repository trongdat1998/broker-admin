package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.ShareConfigDTO;
import io.bhex.broker.admin.controller.param.ShareConfigPO;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 2019/7/1 11:30 AM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
public interface ShareConfigService {

    ShareConfigDTO getShareConfigInfo(Long brokerId);

    Boolean saveShareConfigInfo(ShareConfigPO param);
}
