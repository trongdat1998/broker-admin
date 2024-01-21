package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.AutoAirdropDTO;
import io.bhex.broker.admin.controller.param.AutoAirdropPO;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 30/11/2018 3:51 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface AutoAirdropService {

    Boolean saveAutoAirdrop(AutoAirdropPO param);

    AutoAirdropDTO getAutoAirdrop(Long brokerId);
}
