package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.CommonConfigDTO;
import io.bhex.broker.admin.controller.param.CommonConfigPO;

public interface CommonConfigService {

    CommonConfigDTO setCommonConfig(Long orgId, CommonConfigPO po);

    CommonConfigDTO getCommonConfig(Long orgId, String key);

}
