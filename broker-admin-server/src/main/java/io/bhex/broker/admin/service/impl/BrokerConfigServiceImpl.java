package io.bhex.broker.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.ConfigSwitchReply;
import io.bhex.base.common.EditReply;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.controller.dto.BrokerFeatureConfigDTO;
import io.bhex.broker.admin.controller.dto.BrokerLocalConfigDTO;
import io.bhex.broker.admin.controller.dto.BrokerWholeConfigDTO;
import io.bhex.broker.admin.controller.dto.IndexCustomerConfigDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.BrokerFeatureConfigPO;
import io.bhex.broker.admin.controller.param.BrokerLocalConfigPO;
import io.bhex.broker.admin.controller.param.SaveBrokerWholeConfigPO;
import io.bhex.broker.admin.grpc.client.BrokerConfigClient;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.service.BrokerConfigService;
import io.bhex.broker.grpc.admin.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 4:06 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class BrokerConfigServiceImpl implements BrokerConfigService {

    private static final String INTERNAL_BROKER_URL_TEMPLATE = "https://broker-%s.bhop.cloud";

    @Autowired
    private BrokerConfigClient brokerConfigClient;
    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;


    @Override
    public BrokerWholeConfigDTO getBrokerWholeConfig(Long brokerId) {
        GetBrokerConfigRequest request = GetBrokerConfigRequest.newBuilder()
                .setBrokerId(brokerId)
                .build();
        AdminBrokerConfigDetail wholeConfig = brokerConfigClient.getBrokerWholeConfig(request);
        BrokerWholeConfigDTO wholeConfigDTO = new BrokerWholeConfigDTO();
        BeanUtils.copyProperties(wholeConfig, wholeConfigDTO);
        Map<String, String> extraContactMap = wholeConfig.getExtraContactInfoMap();
        wholeConfigDTO.setMedium(extraContactMap.getOrDefault("medium", ""));
        wholeConfigDTO.setLinkedin(extraContactMap.getOrDefault("linkedin", ""));
        wholeConfigDTO.setGithub(extraContactMap.getOrDefault("github", ""));
        wholeConfigDTO.setDiscord(extraContactMap.getOrDefault("discord", ""));
        wholeConfigDTO.setLine(extraContactMap.getOrDefault("line", ""));
        wholeConfigDTO.setBiyong(extraContactMap.getOrDefault("biyong", ""));
        wholeConfigDTO.setQq(extraContactMap.getOrDefault("qq", ""));
        wholeConfigDTO.setCoinmarketcap(extraContactMap.getOrDefault("coinmarketcap", ""));
        wholeConfigDTO.setCoingecko(extraContactMap.getOrDefault("coingecko", ""));
        wholeConfigDTO.setMyToken(extraContactMap.getOrDefault("myToken", ""));
        wholeConfigDTO.setFeixiaohao(extraContactMap.getOrDefault("feixiaohao", ""));

        if (!StringUtils.isEmpty(wholeConfig.getFunctionConfig())) {
            wholeConfigDTO.setFunctionConfig(JSON.parseObject(wholeConfig.getFunctionConfig(), BrokerWholeConfigDTO.FunctionConfig.class));
        }
        List<AdminLocaleDetail> localeDetailsList = wholeConfig.getLocaleDetailsList();
        //locale footConfig
        if (!CollectionUtils.isEmpty(localeDetailsList)) {
            List<BrokerWholeConfigDTO.FootConfig> footConfigList = new ArrayList<>();
            List<BrokerWholeConfigDTO.HeadConfig> headerConfigList = new ArrayList<>();
            List<BrokerLocalConfigDTO> localConfigDTOS = localeDetailsList.stream().map(c -> {
                if (!StringUtils.isEmpty(c.getFootConfig())) {
                    footConfigList.add(JSON.parseObject(c.getFootConfig(), BrokerWholeConfigDTO.FootConfig.class));
                }
                if (!StringUtils.isEmpty(c.getHeadConfig())) {
                    headerConfigList.add(JSON.parseObject(c.getHeadConfig(), BrokerWholeConfigDTO.HeadConfig.class));
                }
                BrokerLocalConfigDTO localConfigDTO = new BrokerLocalConfigDTO();
                BeanUtils.copyProperties(c, localConfigDTO);
                List<AdminFeatureDetail> featureDetailsList = c.getFeatureDetailsList();
                if (!CollectionUtils.isEmpty(featureDetailsList)) {
                    List<BrokerFeatureConfigDTO> featureConfigDTOS = featureDetailsList.stream().map(d -> {
                        BrokerFeatureConfigDTO featureConfigDTO = new BrokerFeatureConfigDTO();
                        BeanUtils.copyProperties(d, featureConfigDTO);
                        return featureConfigDTO;
                    }).collect(Collectors.toList());
                    localConfigDTO.setFeatureConfigList(featureConfigDTOS);
                }
                return localConfigDTO;
            }).collect(Collectors.toList());
            wholeConfigDTO.setLocalConfigList(localConfigDTOS);
            wholeConfigDTO.setFootConfigList(footConfigList);
            wholeConfigDTO.setHeadConfigList(headerConfigList);
        }
        wholeConfigDTO.setInternalHost(String.format(INTERNAL_BROKER_URL_TEMPLATE, brokerId));
        return wholeConfigDTO;
    }

    @Override
    public Boolean saveBrokerWholeConfig(SaveBrokerWholeConfigPO param, SaveOrPreview saveOrPreview) {
        SaveBrokerConfigRequest.Builder builder = SaveBrokerConfigRequest.newBuilder();
        builder.setSaveType(saveOrPreview.getSaveType());
        builder.setBrokerId(param.getBrokerId());
        param.setDomainHost("");
        AdminBrokerConfigDetail.Builder basicBuilder = AdminBrokerConfigDetail.newBuilder();
        BeanUtils.copyProperties(param, basicBuilder);
        Map<String, String> extraContactMap = new HashMap<>();
        extraContactMap.put("medium", Strings.nullToEmpty(param.getMedium()));
        extraContactMap.put("linkedin", Strings.nullToEmpty(param.getLinkedin()));
        extraContactMap.put("github", Strings.nullToEmpty(param.getGithub()));
        extraContactMap.put("discord", Strings.nullToEmpty(param.getDiscord()));
        extraContactMap.put("line", Strings.nullToEmpty(param.getLine()));
        extraContactMap.put("biyong", Strings.nullToEmpty(param.getBiyong()));
        extraContactMap.put("qq", Strings.nullToEmpty(param.getQq()));
        extraContactMap.put("coinmarketcap", Strings.nullToEmpty(param.getCoinmarketcap()));
        extraContactMap.put("coingecko", Strings.nullToEmpty(param.getCoingecko()));
        extraContactMap.put("myToken", Strings.nullToEmpty(param.getMyToken()));
        extraContactMap.put("feixiaohao", Strings.nullToEmpty(param.getFeixiaohao()));

        basicBuilder.putAllExtraContactInfo(extraContactMap);
        basicBuilder.setLogoUrl(param.getLogoUrl().trim());
        List<BrokerLocalConfigPO> localConfigList = param.getLocalConfigList();

        if (!CollectionUtils.isEmpty(localConfigList)) {
            List<AdminLocaleDetail> adminLocaleDetails = prcessLocaleDetail(localConfigList, param);
            basicBuilder.addAllLocaleDetails(adminLocaleDetails);
        }
        builder.setBrokerConfigDetail(basicBuilder.build());

        return brokerConfigClient.saveBrokerWholeConfig(builder.build());
    }

    private List<AdminLocaleDetail> prcessLocaleDetail(List<BrokerLocalConfigPO> configs, SaveBrokerWholeConfigPO param) {
        if (!CollectionUtils.isEmpty(configs)) {
            List<String> allLocales = configs.stream().map(c -> c.getLocale()).collect(Collectors.toList());

            List<SaveBrokerWholeConfigPO.FootConfig> footConfigList = param.getFootConfigList();
            if (!CollectionUtils.isEmpty(footConfigList)) {
                for (SaveBrokerWholeConfigPO.FootConfig footConfig : footConfigList) {
                    if (!allLocales.contains(footConfig.getLocale())) {
                        allLocales.add(footConfig.getLocale());
                    }
                }
            }

            List<SaveBrokerWholeConfigPO.HeadConfig> headerConfigList = param.getHeadConfigList();
            if (!CollectionUtils.isEmpty(headerConfigList)) {
                for (SaveBrokerWholeConfigPO.HeadConfig headerConfig : headerConfigList) {
                    if (!allLocales.contains(headerConfig.getLocale())) {
                        allLocales.add(headerConfig.getLocale());
                    }
                }
            }

            for (String locale : allLocales) {
                boolean existLocale = configs.stream().anyMatch(c -> c.getLocale().equals(locale));
                if (!existLocale) {
                    BrokerLocalConfigPO po = new BrokerLocalConfigPO();
                    po.setLocale(locale);
                    po.setEnable(1);
                    po.setFeatureConfigList(new ArrayList<>());
                    configs.add(po);
                }
            }

            List<AdminLocaleDetail> details = configs.stream().map(c -> {
                AdminLocaleDetail.Builder builder = AdminLocaleDetail.newBuilder();
                //头部导航和尾部导航处理
                if (!CollectionUtils.isEmpty(footConfigList)) {
                    for (SaveBrokerWholeConfigPO.FootConfig footConfig : footConfigList) {
                        if (c.getLocale().equals(footConfig.getLocale())) {
                            c.setFootConfig(JSON.toJSONString(footConfig));
                            break;
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(headerConfigList)) {
                    for (SaveBrokerWholeConfigPO.HeadConfig headerConfig : headerConfigList) {
                        if (c.getLocale().equals(headerConfig.getLocale())) {
                            c.setHeadConfig(JSON.toJSONString(headerConfig));
                            break;
                        }
                    }
                }

                BeanUtils.copyProperties(c, builder);


                List<AdminFeatureDetail> featureDetails = processFeatureDetail(c.getFeatureConfigList());
                builder.addAllFeatureDetails(featureDetails);
                return builder.build();
            }).collect(Collectors.toList());
            return details;
        }
        return new ArrayList<>();
    }

    private List<AdminFeatureDetail> processFeatureDetail(List<BrokerFeatureConfigPO> configs) {
        if (!CollectionUtils.isEmpty(configs)) {
            List<AdminFeatureDetail> details = configs.stream().map(c -> {
                AdminFeatureDetail.Builder builder = AdminFeatureDetail.newBuilder();
                BeanUtils.copyProperties(c, builder);
                return builder.build();
            }).collect(Collectors.toList());
            return details;
        }
        return new ArrayList<>();
    }

    @Override
    public SaveConfigReply editIndexCustomerConfig(long brokerId, IndexCustomerConfigDTO request, int configType) {
        EditIndexCustomerConfigRequest.Builder builder = EditIndexCustomerConfigRequest.newBuilder();

        List<IndexCustomerConfigDTO.Item> items = request.getItems();
        for (IndexCustomerConfigDTO.Item item : items) {
            List<IndexCustomerConfigDTO.ContentData> contentList = item.getContentlist();
            if (!CollectionUtils.isEmpty(contentList)) {
                for (IndexCustomerConfigDTO.ContentData contentData : contentList) {
                    builder.addConfigs(buildIndexCustomerConfig(brokerId, item, contentData, configType));
                }
            } else {
                builder.addConfigs(buildIndexCustomerConfig(brokerId, item, null, configType));
            }
        }
        SaveConfigReply saveConfigReply = brokerConfigClient.editIndexCustomerConfig(builder.build());
        return saveConfigReply;
    }

    private IndexCustomerConfig buildIndexCustomerConfig(long brokerId, IndexCustomerConfigDTO.Item item, IndexCustomerConfigDTO.ContentData contentData, int configType) {
        IndexCustomerConfig.Builder configBuilder = IndexCustomerConfig.newBuilder();
        configBuilder.setOpen(item.getOpen());
        configBuilder.setBrokerId(brokerId);
        configBuilder.setStatus(item.getStatus());
        configBuilder.setModuleName(item.getModuleName());
        if (contentData != null) {
            configBuilder.setLocale(Strings.nullToEmpty(contentData.getLocale()));
            configBuilder.setContent(Strings.nullToEmpty(contentData.getContent()));
            configBuilder.setEnable(contentData.getEnable());
            configBuilder.setType(contentData.getType() != null ? contentData.getType() : 0);
            configBuilder.setTabName(Strings.nullToEmpty(contentData.getTabName()));
            configBuilder.setConfigTypeValue(configType);
            if (!CollectionUtils.isEmpty(contentData.getPlatform())) {
                configBuilder.setPlatform(String.join(",", contentData.getPlatform().stream().map(p -> p + "").collect(Collectors.toList())));
            }
            if (contentData.getUseModule() > 0) {
                configBuilder.setUseModule(contentData.getUseModule() + "");
            }
        } else { //没有多语言配置的都是要设置使用的
            configBuilder.setEnable(true);
        }
        return configBuilder.build();
    }

    @Override
    public IndexCustomerConfigDTO getIndexCustomerConfig(long brokerId, int status, int configType) {
        GetIndexCustomerConfigRequest request = GetIndexCustomerConfigRequest.newBuilder()
                .setBrokerId(brokerId).setStatus(status)
                .build();
        List<IndexCustomerConfig> configs = brokerConfigClient.getIndexCustomerConfig(request);

        Map<String, List<IndexCustomerConfig>> groupMap = configs.stream()
                .collect(Collectors.groupingBy(IndexCustomerConfig::getModuleName));

        List<IndexCustomerConfigDTO.Item> items = new ArrayList<>();
        for (String moduleName : groupMap.keySet()) {
            IndexCustomerConfigDTO.Item item = new IndexCustomerConfigDTO.Item();
            item.setModuleName(moduleName);

            List<IndexCustomerConfigDTO.ContentData> contentList = new ArrayList<>();
            for (IndexCustomerConfig grpcConfig : groupMap.get(moduleName)) {
                item.setOpen(grpcConfig.getOpen());
                item.setStatus(grpcConfig.getStatus());

                IndexCustomerConfigDTO.ContentData contentData = new IndexCustomerConfigDTO.ContentData();
                if (!StringUtils.isEmpty(grpcConfig.getLocale())) {
                    contentData.setLocale(grpcConfig.getLocale());
                }
                contentData.setContent(grpcConfig.getContent());
                if (!StringUtils.isEmpty(grpcConfig.getTabName())) {
                    contentData.setTabName(grpcConfig.getTabName());
                }
                if (grpcConfig.getType() > 0) {
                    contentData.setType(grpcConfig.getType());
                }
                if (!StringUtils.isEmpty(grpcConfig.getPlatform())) {
                    List<Integer> platform = Arrays.stream(grpcConfig.getPlatform().split(","))
                            .map(p -> Integer.parseInt(p)).collect(Collectors.toList());
                    contentData.setPlatform(platform);
                }
                if (!StringUtils.isEmpty(grpcConfig.getUseModule())) {
                    contentData.setUseModule(Integer.parseInt(grpcConfig.getUseModule()));
                }
                contentList.add(contentData);
                item.setContentlist(contentList);
            }
            items.add(item);
        }
        IndexCustomerConfigDTO indexCustomerConfigDTO = new IndexCustomerConfigDTO();
        indexCustomerConfigDTO.setItems(items);

        return indexCustomerConfigDTO;
    }

    @Override
    public Boolean switchIndexToNewVersion(AdminUserReply userReply, boolean init) {
        long brokerId = userReply.getOrgId();
        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.CUSTOM_CONFIG_GROUP);
        configPO.setKey(BizConstant.INDEX_NEW_VERSION);
        configPO.setValue("true");
        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(null);
        configPO.setWithLanguage(true);
        configPO.setStatus(1);
        ConfigSwitchReply configSwitchReply = baseConfigService.getSwitchConfig(brokerId, configPO);
        if (configSwitchReply.getOpen()) {
            log.warn("org:{} has switched", userReply.getOrgId());
            return true;
        }


        EditReply reply = baseConfigService.editConfig(userReply.getOrgId(), configPO, userReply);
        if (init) {
            return reply.getCode() == 0;
        }
        return brokerConfigClient.switchIndexCustomerConfig(SwitchIndexCustomerConfigRequest
                .newBuilder().setBrokerId(brokerId).setSwitchNewVersion(true).build())
                .getResult();
    }
}
