package io.bhex.broker.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import io.bhex.base.admin.ListAllAuthByUserIdReply;
import io.bhex.base.admin.common.AccountType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.BaseConfigMeta;
import io.bhex.base.common.GetConfigMetasReply;
import io.bhex.base.common.GetConfigMetasRequest;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.service.AdminRoleAuthService;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.filter.XssShieldUtil;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.grpc.client.BaseConfigClient;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseConfigServiceAbstract {

    @Autowired
    @Qualifier(value = "baseConfigClient")
    private BaseConfigClient baseConfigClient;

    @Autowired
    @Qualifier(value = "bhBaseConfigClient")
    private BaseConfigClient bhBaseConfigClient;
    @Autowired
    private BrokerUserClient brokerUserClient;


    @Autowired
    private AdminRoleAuthService roleAuthService;

    protected BaseConfigClient getClient(String opPlatform) {
        if (!StringUtils.isEmpty(opPlatform) && opPlatform.equals(BaseConfigPO.OP_PLATFORM_BH)) {
            return bhBaseConfigClient;
        }
        return baseConfigClient;
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        String strF = str.replaceAll("_", "");
        String strFormat = strF.replaceAll("\\.", "");
        if ("".equals(strFormat)) {
            return false;
        }

        for (int i = strFormat.length(); --i >= 0;) {
            if (!Character.isDigit(strFormat.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public BaseConfigMeta getConfigMeta(String group, String key, String opPlatform) {
        GetConfigMetasRequest request = GetConfigMetasRequest.newBuilder()
                .setGroup(group)
                .setKey(Strings.nullToEmpty(key))
                .build();
        GetConfigMetasReply reply = getClient(opPlatform).getConfigMetas(request);
        List<BaseConfigMeta> metas = reply.getConfigMetaList();
        return metas.size() == 0 ? null : metas.get(0);
    }

    public Combo2<Boolean, String> validKeyAndValue(String group, String key, String value, AdminUserReply adminUserReply, String opPlatform) {
        GetConfigMetasRequest request = GetConfigMetasRequest.newBuilder()
                .setGroup(group)
               // .setKey(Strings.nullToEmpty(key))
                .build();
        GetConfigMetasReply reply = getClient(opPlatform).getConfigMetas(request);
        List<BaseConfigMeta> metas = reply.getConfigMetaList();
        if (CollectionUtils.isEmpty(metas)) {
            log.error("group:{} key:{} no config info.", group, key);
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }
        Optional<BaseConfigMeta> metaOptional = metas.stream()
                .filter(m -> m.getGroup().equals(group))
                .filter(m -> m.getKey().equals("") || m.getKey().equals(Strings.nullToEmpty(key)))
                .findFirst();
        if (!metaOptional.isPresent()) {
            log.error("group:{} key:{} no config info.", group, key);
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }
        BaseConfigMeta meta = metaOptional.get();
        String authIds = meta.getAuthIds();
        if (!StringUtils.isEmpty(authIds) && adminUserReply.getAccountType() != AccountType.ROOT_ACCOUNT) {

            ListAllAuthByUserIdReply allAuthReply = roleAuthService.listAllAuthByUserId(adminUserReply.getOrgId(), adminUserReply.getId());

            boolean canAccess = false;
            List<String> myAuthIds = allAuthReply.getAuthPathInfosList().stream()
                    .map(a -> a.getAuthId() + "")
                    .collect(Collectors.toList());
            for (String authId : authIds.split(",")) {
                if (myAuthIds.contains(authId)) {
                    canAccess = true;
                }
            }
            if (!canAccess) {
                throw new BizException(ErrorCode.NO_PERMISSION, ErrorCode.NO_PERMISSION.getDesc());
            }
        }

        String description = meta.getDescription();
        if (description.startsWith("{") && description.endsWith("}")) {
            JSONObject descriptionJO = JSON.parseObject(meta.getDescription());
            boolean uidKey = descriptionJO.containsKey("uidKey") && descriptionJO.getBoolean("uidKey") != null
                    && descriptionJO.getBoolean("uidKey");
            if (uidKey && !NumberUtil.isDigits(key)) {
                throw new BizException(ErrorCode.USER_IDS_ERROR, ErrorCode.USER_IDS_ERROR.getDesc());
            }
            if (uidKey && brokerUserClient.getBrokerUser(adminUserReply.getOrgId(), Long.parseLong(key)) == null) {
                throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER, ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
            }
        }

        if (!XssShieldUtil.stripXss(value).equals(value)) {
            throw new BizException("bhop.validation.constraints.commoninput.message2");
        }

        String dataLimit = meta.getDataLimit();
        Map<String, String> dataLimitMap = StringUtils.isEmpty(dataLimit) ? new HashMap<>() : JSON.parseObject(dataLimit, Map.class);
        //Decimal String int boolean json
        String dataType = meta.getDataType();
        if (dataType.toLowerCase().equals("int")) {
            Matcher pattern = Pattern.compile("^[+-]?[0-9]+$").matcher(value);
            if (!pattern.find()) {
                log.error("group:{} key:{} value:{} not int.", group, key, value);
                throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
            }
            if (dataLimitMap.containsKey("min")) {
                Integer min = Integer.parseInt(dataLimitMap.get("min"));
                if (Integer.parseInt(value) < min) {
                    log.error("group:{} key:{} value:{} lower than min:{}.", group, key, value, min);
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
                }
            }
            if (dataLimitMap.containsKey("max")) {
                Integer max = Integer.parseInt(dataLimitMap.get("max"));
                if (Integer.parseInt(value) > max) {
                    log.error("group:{} key:{} value:{} greater than max:{}.", group, key, value, max);
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
                }
            }
            if (dataLimitMap.containsKey("enum")) {
                boolean valid = false;
                for (String v : dataLimitMap.get("enum").split(",")) {
                    if (Integer.parseInt(v) == Integer.parseInt(value)) {
                        valid = true;
                    }
                }
                if (!valid) {
                    log.error("group:{} key:{} value:{} not in enum:{}", group, key, value, dataLimitMap.get("enum"));
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
                }
            }
        } else if (dataType.toLowerCase().equals("decimal")) {
            if (!isNumeric(value)) {
                throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
            }
            if (dataLimitMap.containsKey("min")) {
                BigDecimal min = new BigDecimal(dataLimitMap.get("min"));
                if (new BigDecimal(value).compareTo(min) < 0) {
                    log.error("group:{} key:{} value:{} lower than min:{}.", group, key, value, min);
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
                }
            }
            if (dataLimitMap.containsKey("max")) {
                BigDecimal max = new BigDecimal(dataLimitMap.get("max"));
                if (new BigDecimal(value).compareTo(max) > 0) {
                    log.error("group:{} key:{} value:{} greater than max:{}.", group, key, value, max);
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
                }
            }
        } else if (dataType.toLowerCase().equals("json")) {
            if ((!value.trim().startsWith("{") && !value.trim().startsWith("[")) || (!value.trim().endsWith("}") && !value.trim().endsWith("]"))) {
                log.error("group:{} key:{} value:{} not json.", group, key, value);
                throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
            }
        } else if (dataType.toLowerCase().equals("string")) {
            if (dataLimitMap.containsKey("enum")) {
                boolean valid = false;
                for (String v : dataLimitMap.get("enum").split(",")) {
                    if (v.equals(value)) {
                        valid = true;
                    }
                }
                if (!valid) {
                    log.error("group:{} key:{} value:{} not in enum:{}", group, key, value, dataLimitMap.get("enum"));
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
                }
            }
        } else if (dataType.toLowerCase().equals("boolean")) {
            if (!value.toLowerCase().equals("true") && !value.toLowerCase().equals("false")) {
                log.error("group:{} key:{} value:{} not boolean.", group, key, value);
                throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
            }
        }



        return new Combo2<>(true, "");
    }

}
