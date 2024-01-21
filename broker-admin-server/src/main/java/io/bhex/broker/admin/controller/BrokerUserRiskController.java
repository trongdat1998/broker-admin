package io.bhex.broker.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.admin.common.BusinessLog;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.dto.param.QueryLogsPO;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.grpc.client.BusinessLogClient;
import io.bhex.bhop.common.service.AdminUserNameService;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.UserBlackWhiteSettingDTO;
import io.bhex.broker.admin.controller.dto.UserRiskRecordDTO;
import io.bhex.broker.admin.controller.param.SimpleBrokerUserPO;
import io.bhex.broker.admin.controller.param.UserRcRecordsPO;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.admin.GetRcListRequest;
import io.bhex.broker.grpc.admin.GetRcListResponse;
import io.bhex.broker.grpc.bwlist.EditUserBlackWhiteConfigRequest;
import io.bhex.broker.grpc.bwlist.UserBlackWhiteListType;
import io.bhex.broker.grpc.common.Header;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @Description:
 * @Date: 2018/11/7 上午11:48
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class BrokerUserRiskController extends BrokerBaseController {
    @Autowired
    private AdminUserNameService adminUserNameService;
    @Autowired
    private BrokerUserClient brokerUserClient;
    @Resource
    private BusinessLogClient businessLogClient;

    private static Map<String, String> opTypeMap = new HashMap<>();
    static {
        opTypeMap.put("frozen.user.coin.trade", "frozenUserCoinTrade");
        opTypeMap.put("frozen.user.api.coin.trade", "frozenUserApiCoinTrade");
        opTypeMap.put("frozen.user.option.trade", "frozenUserOptionTrade");
        opTypeMap.put("frozen.user.future.trade", "frozenUserFutureTrade");
        opTypeMap.put("frozen.user.otc.trade", "frozenUserOtcTrade");
        opTypeMap.put("frozen.user.bonus.trade", "frozenUserBonusTrade");
        opTypeMap.put("force.audit.user.withdraw", "withdrawForceAudit");
        opTypeMap.put("withdraw.blacklist", "userWithdrawLock");
        opTypeMap.put("withdraw.whitelist", "userWithdrawWhitelist");
        opTypeMap.put("disable.userlogin", "disableUserLogin");
    }


    @BussinessLogAnnotation(opContent = "Frozen Account Login UID:{#po.userId} ", name = "disableUserLogin", entityId = "{#po.userId}")
    @RequestMapping(value = {"/userrisk/disable_user_login", "/brokeruser/disable_user_login"}, method = RequestMethod.POST)
    public ResultModel<Void> disableUserLogin(@RequestBody @Valid SimpleBrokerUserPO po) {

        ResultModel<Void> resultModel = brokerUserClient.disableUserLogin(getOrgId(), po.getUserId());
        return resultModel;
    }

    @BussinessLogAnnotation(opContent = "Unfrozen Account Login UID:{#po.userId} ", name = "reopenUserLogin")
    @RequestMapping(value = {"/userrisk/reopen_user_login", "/brokeruser/reopen_user_login"}, method = RequestMethod.POST)
    public ResultModel<Void> reopenUserLogin(@RequestBody @Valid SimpleBrokerUserPO po) {
        ResultModel<Void> resultModel = brokerUserClient.reopenUserLogin(getOrgId(), po.getUserId());
        return resultModel;
    }


    @BussinessLogAnnotation(entityId = "{#po.userId}", name = "userWithdraw{#po.bwType == 1 ? 'Lock' : 'Whitelist'}",
            opContent = "{#po.status == 1 ? 'set' : 'cancel'} UID:{#po.userId} {#po.bwType == 1 ? 'withdrawal lock' : 'withdraw whitelist'} ")
    @RequestMapping(value = {"/userrisk/user_withdraw_setting", "/brokeruser/user_withdraw_setting"}, method = RequestMethod.POST)
    public ResultModel<Void> userWithdrawSetting(@RequestBody @Valid UserBlackWhiteSettingDTO po, AdminUserReply adminUser) {
        EditUserBlackWhiteConfigRequest.Builder builder = EditUserBlackWhiteConfigRequest.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(po, builder);
        builder.setHeader(Header.newBuilder().setOrgId(adminUser.getOrgId()).build());
        builder.setBwTypeValue(po.getBwType());
        builder.setListTypeValue(UserBlackWhiteListType.WITHDRAW_BLACK_WHITE_LIST_TYPE_VALUE);
        builder.setExtraInfo("{\"global\":true,\"tokens\":[]}"); //默认操作所有币对
        builder.setReason(Strings.nullToEmpty(po.getRemark()));

        brokerUserClient.editUserBlackWhiteConfig(builder.build());
        return ResultModel.ok();
    }

    @RequestMapping(value = "/userrisk/user_rc_records", method = RequestMethod.POST)
    public ResultModel<Void> queryUserRiskLogs(@RequestBody @Valid UserRcRecordsPO po) {
        long orgId = getOrgId();
        Combo2<Long, Long> combo2 = null;


        if ((po.getUserId() != null && po.getUserId() > 0) || !StringUtils.isEmpty(po.getEmail()) || !StringUtils.isEmpty(po.getPhone())) {
            combo2 = getUserIdAndAccountId(po, orgId);
            if (combo2 == null) {
                return ResultModel.ok(new ArrayList<>());
            }
        }

        List<GetRcListResponse.Item> grpcRecords = brokerUserClient.getUserRcList(GetRcListRequest.newBuilder()
                .setRcType(po.getRcType())
                .setBrokerId(orgId)
                .setPageSize(po.getPageSize())
                .setFromId(po.getLastId())
                .setUserId(combo2 != null ? combo2.getV1() : 0L)
                .build());
        List<UserRiskRecordDTO> result = grpcRecords.stream().map(r -> {
            UserRiskRecordDTO dto = new UserRiskRecordDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(r, dto);

            if (!po.getRcType().equals("withdraw.blacklist") && !po.getRcType().equals("withdraw.whitelist")) {
                io.bhex.base.admin.common.QueryLogsRequest.Builder builder = io.bhex.base.admin.common.QueryLogsRequest.newBuilder()
                        .setOrgId(orgId)
                        .setPageSize(1)
                        .setWithRequestInfo(true)
                        .setEntityId(r.getUserId() + "")
                        .addOpTypes(opTypeMap.get(po.getRcType()));
                List<BusinessLog> logs = businessLogClient.queryLogs(builder.build());
                if (CollectionUtils.isNotEmpty(logs)) {
                    JSONObject jo = JSONObject.parseObject(logs.get(0).getRequestInfo());
                    dto.setRemark(jo.containsKey("remark") ? jo.get("remark").toString() : "");
                }
            }

            return dto;
        }).collect(Collectors.toList());
        return ResultModel.ok(result);
    }


    @RequestMapping(value = {"/userrisk/query_risk_logs", "/brokeruser/query_risk_logs"}, method = RequestMethod.POST)
    public ResultModel queryRiskLogs(@RequestBody QueryLogsPO po, AdminUserReply adminUser) {

        if (po.getUserId() == null || po.getUserId() == 0) {
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }

        String opType = opTypeMap.get(po.getOpType());

 
        io.bhex.base.admin.common.QueryLogsRequest.Builder builder = io.bhex.base.admin.common.QueryLogsRequest.newBuilder()
                .setOrgId(adminUser.getOrgId())
                .setFromId(po.getLastId() != null ? po.getLastId() : 0)
                .setStartTime(po.getStartTime() != null ? po.getStartTime() : 0)
                .setEndTime(po.getEndTime() != null ? po.getEndTime() : 0)
                .setPageSize(po.getPageSize())
                .setUsername(Strings.nullToEmpty(po.getUsername()))
                .setWithRequestInfo(true)
                .setEntityId(po.getUserId().toString());
        if (StringUtils.isEmpty(opType)) {
            builder.addAllOpTypes(opTypeMap.values());
        } else {
            if (opType.equals("disableUserLogin")) {
                builder.addOpTypes("disableUserLogin").addOpTypes("reopenUserLogin");
            } else {
                builder.addOpTypes(opType);
            }
        }


        List<BusinessLog> logs = businessLogClient.queryLogs(builder.build());

        List<Map<String, Object>> items = logs.stream().map(l -> {
            JSONObject jo = JSONObject.parseObject(l.getRequestInfo());

            Map<String, Object> item = new HashMap<>();
            item.put("id", l.getId());
            item.put("username", adminUserNameService.getAdminName(l.getOrgId(), l.getUsername()));
            item.put("created", l.getCreated());
            item.put("remark", jo.containsKey("remark") ? Strings.nullToEmpty(jo.get("remark").toString()) : "");


            if (l.getOpType().equals("disableUserLogin") || l.getOpType().equals("reopenUserLogin")) { //{"userId":231651338508042240}
                item.put("switchOpen", l.getOpType().equals("disableUserLogin"));
                item.put("userId", jo.getString("userId"));
            } else if (l.getOpType().equals("userWithdrawLock") || l.getOpType().equals("userWithdrawWhitelist")) { //{"bwType":2,"method":"userWithdrawSetting","listType":0,"userId":231651338508042240,"class":"BrokerUserRiskController","status":1}
                item.put("switchOpen", jo.getInteger("status") == 1);
                item.put("userId", jo.getString("userId"));
            } else {
                // "key":"231651338508042240", "status":1, "value":true,"withLanguage":false}
                item.put("switchOpen", jo.getBoolean("value"));
                item.put("userId", jo.getString("key"));
            }
            opTypeMap.forEach((k, v) -> {
                if (v.equals(l.getOpType())) {
                    item.put("opType", k);
                }
            });


            return item;
        }).collect(Collectors.toList());
        return ResultModel.ok(items);
    }

}
