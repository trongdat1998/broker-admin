package io.bhex.broker.admin.service.impl;

import io.bhex.broker.admin.controller.dto.AppIndexModuleDTO;
import io.bhex.broker.admin.grpc.client.impl.AppConfigClient;
import io.bhex.broker.admin.service.AppConfigService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.app_config.AppIndexModuleConfig;
import io.bhex.broker.grpc.app_config.EditAppIndexIconResponse;
import io.bhex.broker.grpc.app_config.EditAppIndexModuleRequest;
import io.bhex.broker.grpc.app_config.ListAppIndexModulesRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppConfigServiceImpl implements AppConfigService {

    @Autowired
    private AppConfigClient appConfigClient;

    @Override
    public AppIndexModuleDTO queryModules(Long orgId, Integer moduleType) {
        List<AppIndexModuleConfig> configs = appConfigClient.listAppIndexModules(ListAppIndexModulesRequest.newBuilder()
                .setOrgId(orgId)
                .setModuleType(moduleType)
                .build()).getAppIndexModuleList();
        if (CollectionUtils.isEmpty(configs)) { //如果没有拿到，取系统默认的
            configs = appConfigClient.listAppIndexModules(ListAppIndexModulesRequest.newBuilder()
                    .setOrgId(0L)
                    .setModuleType(moduleType)
                    .build()).getAppIndexModuleList();
            if (CollectionUtils.isEmpty(configs)) {
                return null;
            }
        }



        AppIndexModuleDTO dto = new AppIndexModuleDTO();
        dto.setModuleType(moduleType);

        Map<String, List<AppIndexModuleConfig>> groups = configs.stream()
                .collect(Collectors.groupingBy(AppIndexModuleConfig::getLanguage));

        List<AppIndexModuleDTO.ModuleDTO> modules = new ArrayList<>();
        for (String language : groups.keySet()) {
            AppIndexModuleDTO.ModuleDTO moduleDTO = new AppIndexModuleDTO.ModuleDTO();
            moduleDTO.setLanguage(language);

            List<AppIndexModuleConfig> moduleConfigs = groups.get(language);
            List<AppIndexModuleDTO.ItemDTO> items = moduleConfigs.stream()
                    .sorted(Comparator.comparing(AppIndexModuleConfig::getCustomOrder).reversed())
                    .map(c -> {
                        AppIndexModuleDTO.ItemDTO itemDTO = new AppIndexModuleDTO.ItemDTO();
                        BeanCopyUtils.copyPropertiesIgnoreNull(c, itemDTO);
                        itemDTO.setJumpType(c.getJumpTypeValue());
                        itemDTO.setLoginShow(c.getLoginShowValue());
                        return itemDTO;
                    }).collect(Collectors.toList());
            moduleDTO.setItems(items);
            modules.add(moduleDTO);
        }
        dto.setModules(modules);
        return dto;
    }

    @Override
    public boolean editModule(Long orgId, AppIndexModuleDTO appIndexModuleDTO) {

        List<AppIndexModuleConfig> configs = new ArrayList<>();

        List<AppIndexModuleDTO.ModuleDTO> modules = appIndexModuleDTO.getModules();
        for (AppIndexModuleDTO.ModuleDTO moduleDTO : modules) {
            List<AppIndexModuleDTO.ItemDTO> items = moduleDTO.getItems();
            for (AppIndexModuleDTO.ItemDTO itemDTO : items) {
                AppIndexModuleConfig.Builder builder = AppIndexModuleConfig.newBuilder();
                BeanCopyUtils.copyPropertiesIgnoreNull(itemDTO, builder);
                builder.setOrgId(orgId);
                builder.setJumpTypeValue(itemDTO.getJumpType() != null ? itemDTO.getJumpType() : 0);
                builder.setLoginShowValue(itemDTO.getLoginShow() != null ? itemDTO.getLoginShow() : 0);
                builder.setLanguage(moduleDTO.getLanguage() != null ? moduleDTO.getLanguage() : Locale.US.toString());
                configs.add(builder.build());
            }
        }
        EditAppIndexIconResponse response = appConfigClient.editAppIndexModule(EditAppIndexModuleRequest.newBuilder().setOrgId(orgId).setModuleType(appIndexModuleDTO.getModuleType()).addAllAppIndexModule(configs).build());
        log.info("req:{} res:{}", appIndexModuleDTO, response);
        return true;
    }
}
