package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.AppIndexModuleDTO;

public interface AppConfigService {

    AppIndexModuleDTO queryModules(Long orgId, Integer moduleType);

    boolean editModule(Long orgId, AppIndexModuleDTO appIndexModulePO);

}
