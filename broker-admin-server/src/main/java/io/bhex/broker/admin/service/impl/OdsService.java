package io.bhex.broker.admin.service.impl;

import com.alibaba.fastjson.JSON;
import io.bhex.base.admin.ListAllAuthByUserIdReply;
import io.bhex.base.admin.common.AccountType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.service.AdminRoleAuthService;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.controller.dto.BaseConfigDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.OdsQueryPo;
import io.bhex.broker.admin.grpc.client.impl.OdsClient;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.statistics.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OdsService {
    @Autowired
    private OdsClient odsClient;
    @Autowired
    @Qualifier("baseConfigService")
    private BaseConfigService baseConfigService;
    @Autowired
    private AdminRoleAuthService roleAuthService;

    public long getDateTime(String dateStr, String formatStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return 0;
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date newdate = null;
        try {
            newdate = format.parse(dateStr);
        } catch (ParseException e) {

        }
        if (newdate == null) {
            return 0;
        }
        return newdate.getTime();
    }

    private Combo2<Long, Long> getDateTime(OdsQueryPo po) {
        long startTime = getDateTime(po.getStartTime(), "yyyy-MM-dd");
        long endTime = getDateTime(po.getEndTime(), "yyyy-MM-dd");
        if (startTime == 0 && po.getTimeUnit().equals("d")) {
            startTime = System.currentTimeMillis() - 30 * 24 * 3600_000L;
        }
        if (endTime == 0 && po.getTimeUnit().equals("d")) {
            endTime = System.currentTimeMillis() - 24 * 3600_000L;
        }
        return new Combo2<>(startTime, endTime);
    }

    public Map<String, List<Map<String, Object>>> queryOdsData(long orgId, OdsQueryPo po, AdminUserReply adminUserReply) {
        checkAuth(adminUserReply, po.getGroup());
        Combo2<Long, Long> combo2 = getDateTime(po);
        long startTime = combo2.getV1();
        long endTime = combo2.getV2();
        QueryOdsDataRequest request = QueryOdsDataRequest.newBuilder().setOrgId(orgId)
                .setGroup(po.getGroup())
                .setLimit((startTime > 0 && endTime > 0) ? 0 : (po.getLimit() > 0 ? po.getLimit() : 30))
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setTimeUnit(!StringUtils.isEmpty(po.getTimeUnit()) ? po.getTimeUnit() : "d")
                .build();
        QueryOdsDataResponse response = odsClient.queryOdsData(request);
        if (response.getDataMapMap().isEmpty()) {
            return new HashMap<>();
        }
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        Map<String, OdsDataList> keysMap = response.getDataMapMap();
        for (String key : keysMap.keySet()) {
            List<OdsData> odsDataList = keysMap.get(key).getOdsDataList();
            List<Map<String, Object>> resultList = odsDataList.stream().map(d -> {
                Map<String, Object> map = new HashMap<>();
                map.put("d", d.getDateStr());
                map.putAll(JSON.parseObject(d.getValue(), Map.class));
                if (map.containsKey("orgId")) {
                    map.remove("orgId");
                }
                return map;
            }).collect(Collectors.toList());
            result.put(key, resultList);
        }
        return result;
    }

    private void checkAuth(AdminUserReply adminUserReply, String key) {
        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.ODS_DATA_GROUP);
        configPO.setKey(key);

        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BH);
        configPO.setLanguage(null);
        configPO.setWithLanguage(false);

        BaseConfigDTO configDTO = baseConfigService.getOneConfig(0L, configPO);
        if (configDTO == null) {
            log.error("broker:{} no config:{}", adminUserReply.getOrgId(), key);
            throw new BizException(ErrorCode.NO_PERMISSION, ErrorCode.NO_PERMISSION.getDesc());
        }
        String extraValue = configDTO.getExtraValue();
        if (StringUtils.isEmpty(extraValue)) {
            throw new BizException(ErrorCode.NO_PERMISSION, ErrorCode.NO_PERMISSION.getDesc());
        }

        Map<String, String> extraMap = JsonUtil.defaultGson().fromJson(extraValue, Map.class);
        String authIds = extraMap.get("authIds");
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
    }

    public Map<String, Map<String, List<Map<String, Object>>>> queryOdsTokenData(long orgId, OdsQueryPo po, AdminUserReply adminUserReply) {

        checkAuth(adminUserReply, po.getGroup());

        Combo2<Long, Long> combo2 = getDateTime(po);
        long startTime = combo2.getV1();
        long endTime = combo2.getV2();
        QueryOdsDataRequest request = QueryOdsDataRequest.newBuilder().setOrgId(orgId)
                .setGroup(po.getGroup())
                .setLimit((startTime > 0 && endTime > 0) ? 0 : (po.getLimit() > 0 ? po.getLimit() : 30))
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setTimeUnit(!StringUtils.isEmpty(po.getTimeUnit()) ? po.getTimeUnit() : "d")
                .build();
        QueryOdsTokenDataResponse response = odsClient.queryOdsTokenData(request);
        if (response.getDataMapMap().isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Map<String, List<Map<String, Object>>>> result = new HashMap<>();
        Map<String, OdsTokenDataMap> keysMap = response.getDataMapMap();
        for (String key : keysMap.keySet()) {
            Map<String, OdsDataList> odsDataMap = keysMap.get(key).getTokenMapMap();
            Map<String, List<Map<String, Object>>> tokenMap = new HashMap<>();
            for (String token : odsDataMap.keySet()) {

                List<Map<String, Object>> resultList = odsDataMap.get(token).getOdsDataList().stream().map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("d", d.getDateStr());
                    map.putAll(JSON.parseObject(d.getValue(), Map.class));
                    if (map.containsKey("orgId")) {
                        map.remove("orgId");
                    }
                    if (map.containsKey("token")) {
                        map.remove("token");
                    }
                    return map;
                }).collect(Collectors.toList());
                tokenMap.put(token, resultList);

            }

            result.put(key, tokenMap);
        }
        return result;
    }

    public Map<String, Map<String, List<Map<String, Object>>>> queryOdsSymbolData(long orgId, OdsQueryPo po, AdminUserReply adminUserReply) {
        checkAuth(adminUserReply, po.getGroup());
        Combo2<Long, Long> combo2 = getDateTime(po);
        long startTime = combo2.getV1();
        long endTime = combo2.getV2();
        QueryOdsDataRequest request = QueryOdsDataRequest.newBuilder().setOrgId(orgId)
                .setGroup(po.getGroup())
                .setLimit((startTime > 0 && endTime > 0) ? 0 : (po.getLimit() > 0 ? po.getLimit() : 30))
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setTimeUnit(!StringUtils.isEmpty(po.getTimeUnit()) ? po.getTimeUnit() : "d")
                .build();
        QueryOdsSymbolDataResponse response = odsClient.queryOdsSymbolData(request);
        if (response.getDataMapMap().isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Map<String, List<Map<String, Object>>>> result = new HashMap<>();
        Map<String, OdsSymbolDataMap> keysMap = response.getDataMapMap();
        for (String key : keysMap.keySet()) {
            Map<String, OdsDataList> odsDataMap = keysMap.get(key).getSymbolMapMap();
            Map<String, List<Map<String, Object>>> symbolMap = new HashMap<>();
            for (String symbol : odsDataMap.keySet()) {

                List<Map<String, Object>> resultList = odsDataMap.get(symbol).getOdsDataList().stream().map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("d", d.getDateStr());
                    map.putAll(JSON.parseObject(d.getValue(), Map.class));
                    if (map.containsKey("orgId")) {
                        map.remove("orgId");
                    }
                    if (map.containsKey("symbol")) {
                        map.remove("symbol");
                    }
                    return map;
                }).collect(Collectors.toList());
                symbolMap.put(symbol, resultList);

            }

            result.put(key, symbolMap);
        }
        return result;
    }

    public List<Map<String, Object>> queryTopData(QueryTopDataRequest request) {
        List<Map<String, Object>> list = odsClient.queryTopData(request).getDataList().stream().map(d -> {
            Map<String, Object> item = new HashMap<>();
            d.getItemMap().forEach((k, v) -> {
                if (k.toLowerCase().endsWith("amount") || k.toLowerCase().endsWith("quantity")) {
                    item.put(k, new BigDecimal(v).stripTrailingZeros().toPlainString());
                } else if (k.equals("index") || k.toLowerCase().endsWith("num")) {
                    item.put(k, Integer.parseInt(v));
                } else {
                    item.put(k, v);
                }
            });
            return item;
        }).collect(Collectors.toList());
        return list;
    }
}
