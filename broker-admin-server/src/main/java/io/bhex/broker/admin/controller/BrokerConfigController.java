package io.bhex.broker.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.EditReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.MD5Util;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.config.AwsPublicStorageConfig;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.controller.dto.BaseConfigDTO;
import io.bhex.broker.admin.controller.dto.BrokerWholeConfigDTO;
import io.bhex.broker.admin.controller.dto.IndexCustomerConfigDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.IndexCustomerStatusPO;
import io.bhex.broker.admin.controller.param.LanguageConfigPO;
import io.bhex.broker.admin.controller.param.SaveBrokerWholeConfigPO;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.service.BrokerConfigService;
import io.bhex.broker.common.objectstorage.CannedAccessControlList;
import io.bhex.broker.common.objectstorage.ObjectStorage;
import io.bhex.broker.grpc.admin.IndexCustomerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 4:44 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/broker")
public class BrokerConfigController extends BaseController {


    @Autowired
    private BrokerConfigService brokerConfigService;

    @Resource(name = "awsPublicStorageConfig")
    private AwsPublicStorageConfig awsPublicStorageConfig;
    @Resource(name = "objecPublictStorage")
    private ObjectStorage awsPublicObjectStorage;
    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;


    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public ResultModel getWholeConfig() {
        AdminUserReply adminUserReply = getRequestUser();
        BrokerWholeConfigDTO brokerWholeConfig = brokerConfigService.getBrokerWholeConfig(adminUserReply.getOrgId());
        return ResultModel.ok(brokerWholeConfig);
    }

    @RequestMapping(value = "/config/preview", method = RequestMethod.POST)
    public ResultModel getPreviewConfig(@RequestBody @Valid SaveBrokerWholeConfigPO param) {
        AdminUserReply adminUserReply = getRequestUser();
        param.setBrokerId(adminUserReply.getOrgId());
        Boolean isOk = brokerConfigService.saveBrokerWholeConfig(param, BrokerConfigService.SaveOrPreview.PREVIEW);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "Save Website Configuration")
    @RequestMapping(value = "/config/submit", method = RequestMethod.POST)
    public ResultModel submitBrokerConfig(@RequestBody @Valid SaveBrokerWholeConfigPO param) {
        AdminUserReply adminUserReply = getRequestUser();
        param.setBrokerId(adminUserReply.getOrgId());
        Boolean isOk = brokerConfigService.saveBrokerWholeConfig(param, BrokerConfigService.SaveOrPreview.SAVE);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "Save Website Configuration")
    @RequestMapping(value = "/index_customer_config/edit", method = RequestMethod.POST)
    public ResultModel submitIndexCustomerConfig(@RequestBody @Valid IndexCustomerConfigDTO po, AdminUserReply userReply) {
        if(CollectionUtils.isEmpty(po.getItems())) {
            return ResultModel.error("empty.items");
        }
        brokerConfigService.editIndexCustomerConfig(userReply.getOrgId(), po, IndexCustomerConfig.ConfigType.WEB_VALUE);
        return ResultModel.ok();
    }

    @RequestMapping(value = "/index_customer_config/query", method = RequestMethod.POST)
    public ResultModel getIndexCustomerConfig(AdminUserReply userReply,
                                              @RequestBody @Valid IndexCustomerStatusPO po) {
        return ResultModel.ok(brokerConfigService.getIndexCustomerConfig(userReply.getOrgId(), po.getStatus(), IndexCustomerConfig.ConfigType.WEB_VALUE));
    }

    @BussinessLogAnnotation(opContent = "Save APP Index Configuration")
    @RequestMapping(value = "/app_index_customer_config/edit", method = RequestMethod.POST)
    public ResultModel submitAppIndexCustomerConfig(@RequestBody @Valid IndexCustomerConfigDTO po, AdminUserReply userReply) {
        brokerConfigService.editIndexCustomerConfig(userReply.getOrgId(), po, IndexCustomerConfig.ConfigType.APP_VALUE);
        return ResultModel.ok();
    }

    @RequestMapping(value = "/app_index_customer_config/query", method = RequestMethod.POST)
    public ResultModel getAppIndexCustomerConfig(AdminUserReply userReply,
                                              @RequestBody @Valid IndexCustomerStatusPO po) {
        return ResultModel.ok(brokerConfigService.getIndexCustomerConfig(userReply.getOrgId(), po.getStatus(), IndexCustomerConfig.ConfigType.APP_VALUE));
    }


    @RequestMapping(value = "/index_customer_config/switch", method = RequestMethod.POST)
    public ResultModel switchIndexVersionConfig(AdminUserReply userReply) {
        brokerConfigService.switchIndexToNewVersion(userReply, false);
        return ResultModel.ok();
    }


    @BussinessLogAnnotation(opContent = "Config Default {#po.language} Language Pack")
    @RequestMapping(value = "/language/config_default_language", method = RequestMethod.POST)
    public ResultModel configDefaultLanguage(@RequestBody @Valid LanguageConfigPO po) {
        String jsonStr = po.getJson();
        if(Strings.isNullOrEmpty(jsonStr) || jsonStr.equals("{}")) {
            return ResultModel.error("empty.items");
        }
        Map<String, String> reqMap = JSON.parseObject(po.getJson(), Map.class);
        if (CollectionUtils.isEmpty(reqMap)) {
            return ResultModel.error("empty.items");
        }
        if (!po.getLanguage().equals(Locale.US.toString())) {
            Map<String, String> items = JSON.parseObject(po.getJson(), Map.class);
            Map<String, String> usItems = getJsonContentMap(0L, po.getTab(), Locale.US.toString());

            for (String uskey : usItems.keySet()) { //如果上传来的里面没有的key，从英语里面copy一份
                if (!items.containsKey(uskey)) {
                    items.put(uskey, usItems.get(uskey));
                }
            }
            jsonStr = JSON.toJSONString(items);
        }


        String url = saveConfig(0, jsonStr, po.getLanguage(), po.getTab());
        return ResultModel.ok(url);
    }


    private String getJsParameterContent(long orgId) {
        if (orgId == 0) {
            return "window.WEB_LOCALES=";
        }
        return "window.WEB_LOCALES_USER=";
    }

    private Map<String, String> getJsonContentMap(long orgId, String tab, String language) {

        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.SITE_LANGUAGE_GROUP);
        configPO.setKey(tab);

        configPO.setOpPlatform(orgId == 0 ? BaseConfigPO.OP_PLATFORM_BH : BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(language);
        configPO.setWithLanguage(true);


        BaseConfigDTO configDTO = baseConfigService.getOneConfig(orgId, configPO);
        if (configDTO == null) {
            return new HashMap<>();
        }

        String fileKey = "language" + configDTO.getValue().toString().split("language")[1];
        if (!awsPublicObjectStorage.doesObjectExists(fileKey)) {
            return new HashMap<>();
        }
        byte[] bytes = awsPublicObjectStorage.downloadObject(fileKey);
        Map<String, String> map = JSON.parseObject(new String(bytes).split(getJsParameterContent(orgId))[1], Map.class);
        return map;
    }


    @BussinessLogAnnotation(opContent = "Config {#po.language} Language Pack")
    @RequestMapping(value = "/language/config_language", method = RequestMethod.POST)
    public ResultModel languageConfig(@RequestBody @Valid LanguageConfigPO po) {
        Long orgId = getOrgId();
        String jsonStr = po.getJson();
        if(Strings.isNullOrEmpty(jsonStr) || jsonStr.equals("{}")) {
            return ResultModel.error("empty.items");
        }
        Map<String, String> reqMap = JSON.parseObject(po.getJson(), Map.class);
        if (CollectionUtils.isEmpty(reqMap)) {
            return ResultModel.error("empty.items");
        }
        
        String url = saveConfig(orgId, jsonStr, po.getLanguage(), po.getTab());
        return ResultModel.ok(url);
    }

    @RequestMapping(value = "/language/get_default_language_config", method = RequestMethod.POST)
    public ResultModel getDefaultLanguageConfig(@RequestBody @Valid LanguageConfigPO po) {
        Map<String, String> jsonMap = getJsonContentMap(0L, po.getTab(), po.getLanguage());
        return ResultModel.ok(JSON.toJSONString(jsonMap));
    }


    @RequestMapping(value = "/language/get_language_config", method = RequestMethod.POST)
    public ResultModel getLanguageConfig(@RequestBody @Valid LanguageConfigPO po) {
        Map<String, String> jsonMap = getJsonContentMap(getOrgId(), po.getTab(), po.getLanguage());
        return ResultModel.ok(JSON.toJSONString(jsonMap));
    }


    private String saveConfig(long orgId, String jsonStr, String language, String tab) {
        String fileKey = "language/" + orgId + "/" + MD5Util.getMD5(jsonStr) + "_" + language + ".js";


        String url = awsPublicStorageConfig.getStaticUrl() + fileKey;
        byte[] content = (getJsParameterContent(orgId) + jsonStr).getBytes();
        InputStream is = new ByteArrayInputStream(content);
        awsPublicObjectStorage.uploadObjectWithCacheControl(fileKey, MediaType.JSON_UTF_8, is, CannedAccessControlList.PublicRead, "max-age=31536000");

        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.SITE_LANGUAGE_GROUP);
        configPO.setKey(tab);
        configPO.setValue(url);
        configPO.setOpPlatform(orgId == 0 ? BaseConfigPO.OP_PLATFORM_BH : BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(language);
        configPO.setWithLanguage(true);
        configPO.setStatus(1);

        EditReply reply = baseConfigService.editConfig(orgId, configPO, getRequestUser());

        log.info("filekey org:{} tab:{} filekey:{} reply:{}", orgId, tab, fileKey, reply);
        return url;
    }


}
