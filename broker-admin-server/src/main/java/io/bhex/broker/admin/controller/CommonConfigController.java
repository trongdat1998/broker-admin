package io.bhex.broker.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.bhex.base.admin.common.AccountType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.BaseConfigMeta;
import io.bhex.base.common.ConfigSwitchReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.constants.OpTypeConstant;
import io.bhex.broker.admin.controller.dto.BaseConfigDTO;
import io.bhex.broker.admin.controller.dto.CommonConfigDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.BatchBaseConfigPO;
import io.bhex.broker.admin.controller.param.CommonConfigPO;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.service.CommonConfigService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/broker/config/common_config")
public class CommonConfigController extends BaseController {

    @Resource
    private CommonConfigService commonConfigService;

    @BussinessLogAnnotation(name = OpTypeConstant.SET_COMMON_CONFIG, entityId = "{#po.key}", remark = "{#po.desc}")
    @RequestMapping(value = "/set", method = RequestMethod.POST)
    public ResultModel<CommonConfigDTO> setCommonConfig(@RequestBody @Valid CommonConfigPO po) {
        CommonConfigDTO commonConfig = commonConfigService.setCommonConfig(getOrgId(), po);
        return ResultModel.ok(commonConfig);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultModel<CommonConfigDTO> getCommonConfig(String key) {
        CommonConfigDTO commonConfig = commonConfigService.getCommonConfig(getOrgId(), key);
        return ResultModel.ok(commonConfig);
    }



    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;

    @Autowired
    @Qualifier(value = "baseSymbolConfigService")
    private BaseConfigService baseSymbolConfigService;

    @Autowired
    @Qualifier(value = "baseTokenConfigService")
    private BaseConfigService baseTokenConfigService;



    private BaseConfigService getConfigService(BaseConfigPO po) {
        if (!StringUtils.isEmpty(po.getToken())) {
            return baseTokenConfigService;
        }

        if (!StringUtils.isEmpty(po.getSymbol())) {
            return baseSymbolConfigService;
        }

        return baseConfigService;
    }

    private BaseConfigMeta convertBaseConfigPO(BaseConfigPO po) {
        BaseConfigMeta configMeta = getConfigService(po).getConfigMeta(po.getGroup(), po.getKey(), po.getOpPlatform());
        if (configMeta == null) {
            configMeta = getConfigService(po).getConfigMeta(po.getGroup(), "", po.getOpPlatform());
        }
        if (configMeta == null) {
            throw new io.bhex.bhop.common.exception.BizException(ErrorCode.ERR_REQUEST_PARAMETER, "group name error!");
        }
        
        if (!StringUtils.isEmpty(po.getLanguage())) {
            po.setWithLanguage(true);
        } else {
            po.setWithLanguage(false);
        }
        if (po.getGroup().endsWith(".bh")) {
            po.setOpPlatform(BaseConfigPO.OP_PLATFORM_BH);
        }

        return configMeta;
    }

    @AccessAnnotation(verifyAuth = false)
    @BussinessLogAnnotation
    @RequestMapping(value = "/edit_aggerate_config", method = RequestMethod.POST)
    public ResultModel editAggerateConfig(@RequestBody @Valid BaseConfigPO reqPo, AdminUserReply adminUserReply) {
        BaseConfigPO po = new BaseConfigPO();
        BeanCopyUtils.copyPropertiesIgnoreNull(reqPo, po);
        BaseConfigMeta configMeta = convertBaseConfigPO(po);
        JSONObject configMetaJO = JSON.parseObject(configMeta.getDescription());

        Object obj = po.getValue();
        if (!(obj instanceof List)) {
            return ResultModel.errorParameter("value", "");
        }

        List<Map<String, Object>> list = (List) obj;
        if(CollectionUtils.isEmpty(list)) {
            return ResultModel.error("empty.items");
        }
        for (Map<String, Object> item : list) {
            po.setStatus(MapUtils.getBoolean(item, "enabled") ? 1 : 0);
            String language = MapUtils.getString(item, "language");
            if (!StringUtils.isEmpty(language)) {
                language = language.split("-")[0] + "_" + language.split("-")[1].toUpperCase();
                po.setLanguage(language);
                po.setWithLanguage(true);
            }
            item.remove("language");
            item.remove("enabled");
            po.setValue(JSON.toJSONString(item));
            getConfigService(po).editConfig(adminUserReply.getOrgId(), po, adminUserReply);
        }

        saveBizLog("editAggerateConfig", configMetaJO.getString("opContent") + " key:" + po.getKey(), JSON.toJSONString(reqPo), 0);

        return ResultModel.ok();
    }

    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/get_aggerate_config", method = RequestMethod.POST)
    public ResultModel getBaseConfig(@RequestBody @Valid BaseConfigPO po) {
        long orgId = getOrgId();
        //long orgId = 6001L;

        convertBaseConfigPO(po);

        List<BaseConfigDTO> items = getConfigService(po).getConfigs(orgId, po, Arrays.asList(po.getKey()));

        Map<String, Object> result = new HashMap<>();
        result.put("group", po.getGroup());
        result.put("key", po.getKey());

        List<Map<String, Object>> valueList = new ArrayList<>();
        for (BaseConfigDTO dto : items) {
            Map<String, Object> item;
            String v = dto.getValue();
            if (v.startsWith("{") && v.endsWith("}")) {
                item = JSON.parseObject(v, Map.class);
            } else {
                item = new HashMap<>();
                item.put("v", v);
            }
            if (!StringUtils.isEmpty(dto.getLanguage())) {
                String language = dto.getLanguage();
                language = language.split("_")[0] + "-" + language.split("_")[1].toLowerCase();
                item.put("language", language);
            }
            item.put("enabled", true);
            valueList.add(item);
        }
        result.put("value", valueList);
        return ResultModel.ok(result);
    }


    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/edit_config", method = RequestMethod.POST)
    public ResultModel editBaseConfig(@RequestBody @Valid BaseConfigPO po, AdminUserReply adminUserReply) {
        //AdminUserReply adminUserReply = AdminUserReply.newBuilder().setOrgId(6001L).setAccountType(AccountType.ROOT_ACCOUNT).setUsername("").build();
        BaseConfigMeta configMeta = convertBaseConfigPO(po);
        getConfigService(po).editConfig(adminUserReply.getOrgId(), po, adminUserReply);



        JSONObject jo = JSON.parseObject(configMeta.getDescription());
        String content = jo.getString("opContent");

        if (configMeta.getDataType().equalsIgnoreCase("boolean")) {
            //boolean uidKey = jo.containsKey("uidKey") && jo.getBoolean("uidKey") != null && jo.getBoolean("uidKey");
            //content =  (po.getValue().toString().equals("true") ? "Open " : "Close ") + (uidKey ? po.getKey() : "") + content;
            content =  (po.getValue().toString().equals("true") ? "Open " : "Close ") + po.getKey() + " " + content;

        } else if (configMeta.getDataType().equals("json")) {
            JSONObject valueJO = JSON.parseObject(po.getValue().toString());
            Matcher matcher = PARAM_PLACEHOLDER_PATTERN.matcher(content);
            while (matcher.find()) {
                String key = matcher.group();
                String realKey = key.replace("{", "").replace("}", "");
                content = content.replace(key, valueJO.getString(realKey));
            }
        }
        boolean uidKey = jo.containsKey("uidKey") && jo.getBoolean("uidKey") != null && jo.getBoolean("uidKey");
        saveBizLog(jo.getString("opType"), content, JSON.toJSONString(po), 0, uidKey ? po.getKey() : "");
        return ResultModel.ok();
    }

    private static final Pattern PARAM_PLACEHOLDER_PATTERN = Pattern.compile("\\{([A-Za-z_$]+\\.[A-Za-z_$\\d]*)\\}");




    @AccessAnnotation(verifyAuth = false)
    @BussinessLogAnnotation(name = OpTypeConstant.SET_COMMON_CONFIG)
    @RequestMapping(value = "/edit_configs", method = RequestMethod.POST)
    public ResultModel editConfigs(@RequestBody @Valid BatchBaseConfigPO po) {
        AdminUserReply adminUserReply = getRequestUser();
        convertBaseConfigPO(po.getConfigs().get(0));
        getConfigService(po.getConfigs().get(0)).editConfigs(getOrgId(), po, adminUserReply);
        return ResultModel.ok();
    }

    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/get_config", method = RequestMethod.POST)
    public ResultModel getConfig(@RequestBody @Valid BaseConfigPO po, AdminUserReply adminUser) {
        convertBaseConfigPO(po);
        BaseConfigDTO dto = getConfigService(po).getOneConfig(adminUser.getOrgId(), po);
        return ResultModel.ok(dto);
    }

    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/get_group_configs", method = RequestMethod.POST)
    public ResultModel getGroupConfigs(@RequestBody @Valid BaseConfigPO po) {
        convertBaseConfigPO(po);
        List<BaseConfigDTO> list = getConfigService(po).getConfigsByGroup(getOrgId(), po);
        return ResultModel.ok(list);
    }

    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/get_token_configs", method = RequestMethod.POST)
    public ResultModel getTokenGroupConfigs(@RequestBody @Valid BaseConfigPO po) {
        convertBaseConfigPO(po);
        List<BaseConfigDTO> list = baseTokenConfigService.getConfigsByGroup(getOrgId(), po);
        return ResultModel.ok(list);
    }

    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/get_symbol_configs", method = RequestMethod.POST)
    public ResultModel getSymbolGroupConfigs(@RequestBody @Valid BaseConfigPO po) {
        convertBaseConfigPO(po);
        List<BaseConfigDTO> list = baseSymbolConfigService.getConfigsByGroup(getOrgId(), po);
        return ResultModel.ok(list);
    }

    @AccessAnnotation(verifyAuth = false)
    @BussinessLogAnnotation(name = OpTypeConstant.SET_COMMON_CONFIG)
    @RequestMapping(value = "/cancel_config", method = RequestMethod.POST)
    public ResultModel cancelConfig(@RequestBody @Valid BaseConfigPO po) {
        convertBaseConfigPO(po);
        getConfigService(po).cancelConfig(getOrgId(), po, getRequestUser().getUsername());
        return ResultModel.ok();
    }

    @AccessAnnotation(verifyAuth = false)
    @BussinessLogAnnotation(name = OpTypeConstant.SET_COMMON_CONFIG)
    @RequestMapping(value = "/switch_config", method = RequestMethod.POST)
    public ResultModel switchConfig(@RequestBody @Valid BaseConfigPO po) {
        convertBaseConfigPO(po);
        po.setValue(po.getSwitchStatus() + "");
        getConfigService(po).switchConfig(getOrgId(), po, getRequestUser().getUsername());
        return ResultModel.ok();
    }

    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/get_switch_config", method = RequestMethod.POST)
    public ResultModel getSwitchConfig(@RequestBody @Valid BaseConfigPO po) {
        convertBaseConfigPO(po);
        ConfigSwitchReply reply = getConfigService(po).getSwitchConfig(getOrgId(), po);
        Map<String, Boolean> result = new HashMap<>();
        result.put("existed", reply.getExisted());
        result.put("open", reply.getOpen());
        return ResultModel.ok(result);
    }
}
