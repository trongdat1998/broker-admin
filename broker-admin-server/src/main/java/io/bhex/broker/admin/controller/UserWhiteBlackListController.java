package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.UserBlackWhiteSettingDTO;
import io.bhex.broker.admin.controller.param.UserBlackWhiteListPO;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.bwlist.EditUserBlackWhiteConfigRequest;
import io.bhex.broker.grpc.bwlist.GetBlackWhiteConfigsRequest;
import io.bhex.broker.grpc.bwlist.UserBlackWhiteConfig;
import io.bhex.broker.grpc.bwlist.UserBlackWhiteListType;
import io.bhex.broker.grpc.common.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date: 2020/1/6 下午8:06
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1/{path}")
public class UserWhiteBlackListController extends BrokerBaseController {

    @Autowired
    private BrokerUserClient brokerUserClient;

    @RequestMapping(value = "/edit_bw_list", method = RequestMethod.POST)
    public ResultModel editBwList(@RequestBody @Valid UserBlackWhiteSettingDTO po, @PathVariable String path) {
        if (!path.equals("symbol")) {
            return ResultModel.error("request.parameter.error");
        }
        List<Long> userIds = new ArrayList<>();
        if (po.getUserId() > 0) {
            userIds.add(po.getUserId());
        }
        if (!CollectionUtils.isEmpty(po.getUserIds())) {
            userIds.addAll(po.getUserIds());
        }

        if (!CollectionUtils.isEmpty(userIds)) {
            userIds = userIds.stream().filter(u -> u != null).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(userIds)) {
                return ResultModel.error(ErrorCode.USER_IDS_ERROR.getCode(), "UserId Error", "");
            }
            List<Long> errorUserIds = getErrorUserIds(getOrgId(), userIds);
            if (!CollectionUtils.isEmpty(errorUserIds)) {
                return ResultModel.error(ErrorCode.USER_IDS_ERROR.getCode(), "UserId Error", errorUserIds);
            }
        }

        EditUserBlackWhiteConfigRequest.Builder builder = EditUserBlackWhiteConfigRequest.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(po, builder);
        builder.setUserId(0);
        builder.addAllUserIds(userIds);

        builder.setHeader(Header.newBuilder().setOrgId(getOrgId()).build());
        builder.setBwTypeValue(po.getBwType());
        builder.setListType(UserBlackWhiteListType.forNumber(po.getListType()));

        if (po.getListType() == UserBlackWhiteListType.SYMBOL_BAN_SELL_WHITE_LIST_TYPE_VALUE
                || po.getListType() == UserBlackWhiteListType.SYMBOL_BAN_BUY_WHITE_LIST_TYPE_VALUE) {
            // 禁买 禁卖是全量提供userid
            builder.setFullVolumeUser(true);
        }
        builder.setExtraInfo(""); //默认操作所有币对


        brokerUserClient.editUserBlackWhiteConfig(builder.build());
        return ResultModel.ok();
    }

    @RequestMapping(value = "/get_bw_list_userids", method = RequestMethod.POST)
    public ResultModel getUserIds(@RequestBody UserBlackWhiteListPO po, @PathVariable String path) {
        if (po.getBwType() == 0 || po.getListType() == 0) {
            return ResultModel.error("error param");
        }
        List<UserBlackWhiteConfig> configs = getConfigs(po);
        List<Long> userIds = configs.stream().map(c -> c.getUserId()).collect(Collectors.toList());
        return ResultModel.ok(userIds);
    }

    @RequestMapping(value = "/get_bw_list", method = RequestMethod.POST)
    public ResultModel<Void> getBwList(@RequestBody @Valid UserBlackWhiteListPO po, @PathVariable String path) {
        List<UserBlackWhiteConfig> configs = getConfigs(po);
        List<UserBlackWhiteSettingDTO> result = configs.stream().map(c -> {
            UserBlackWhiteSettingDTO dto = new UserBlackWhiteSettingDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(c, dto);
            dto.setBwType(c.getBwTypeValue());
            dto.setListType(c.getListTypeValue());
            dto.setRemark(c.getReason());
            return dto;
        }).collect(Collectors.toList());
        return ResultModel.ok(result);
    }

    private List<UserBlackWhiteConfig> getConfigs(UserBlackWhiteListPO po) {
        GetBlackWhiteConfigsRequest.Builder builder = GetBlackWhiteConfigsRequest.newBuilder();
        builder.setOrgId(getOrgId());
        builder.setFromId(po.getLastId());
        builder.setBwTypeValue(po.getBwType());
        builder.setListTypeValue(po.getListType());
        builder.setPageSize(po.getPageSize());
        builder.setUserId(po.getUserId());
        List<UserBlackWhiteConfig> configs = brokerUserClient.getBlackWhiteConfigs(builder.build());
        return configs;
    }

}
