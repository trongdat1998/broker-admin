package io.bhex.broker.admin.controller;


import com.google.common.collect.Maps;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.InterestConfigDTO;
import io.bhex.broker.admin.controller.dto.UserLevelHaveTokenDiscountDTO;
import io.bhex.broker.admin.controller.dto.UserLevelInfoDTO;
import io.bhex.broker.admin.controller.param.IdPO;
import io.bhex.broker.admin.controller.param.QueryLevelUserPO;
import io.bhex.broker.admin.controller.param.UserLevelConfigPO;
import io.bhex.broker.admin.controller.param.UserLevelWhiteListPO;
import io.bhex.broker.admin.service.MarginService;
import io.bhex.broker.admin.service.impl.UserLevelService;
import io.bhex.broker.grpc.user.level.UserLevelConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 用户等级体系
 * @Date: 2020/4/30 上午11:24
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@RestController
@RequestMapping(value = "/api/v1")
public class UserLevelController extends BrokerBaseController{

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private MarginService marginService;

    @RequestMapping(value = "/user_level/withdraw_limiter")
    public ResultModel queryWithdrawLimiterBtc(AdminUserReply adminUser){
        return ResultModel.ok(userLevelService.getDefaultWithdrawLimiter(adminUser.getOrgId()));
    }

    @RequestMapping(value = "/user_level/config")
    public ResultModel userLevelConfig(@RequestBody @Valid UserLevelConfigPO po, AdminUserReply adminUser){
        UserLevelConfigResponse response = userLevelService.userLevelConfig(adminUser.getOrgId(), false, po);
        Map<String, Long> result = Maps.newHashMap();
        result.put("userCount", response.getUserCount());
        result.put("levelConfigId", response.getLevelConfigId());
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/user_level/preview_config")
    public ResultModel userLevelPreviewConfig(@RequestBody @Valid UserLevelConfigPO po, AdminUserReply adminUser){
        UserLevelConfigResponse response = userLevelService.userLevelConfig(adminUser.getOrgId(), true, po);
        Map<String, Long> result = Maps.newHashMap();
        result.put("userCount", response.getUserCount());
        result.put("levelConfigId", response.getLevelConfigId());
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/user_level/delete_config")
    public ResultModel deleteLevelConfig(@RequestBody @Valid IdPO po, AdminUserReply adminUser){
        userLevelService.deleteUserLevelConfig(adminUser.getOrgId(), po.getId());
        return ResultModel.ok();
    }

    @RequestMapping(value = {"/user_level/query_configs", "/activity/ieo/user_level/user_level_info"} )
    public ResultModel queryLevelConfigs(@RequestBody IdPO po, AdminUserReply adminUser){
        List<UserLevelConfigPO> configs = userLevelService.listUserLevelConfigs(adminUser.getOrgId(),
                po.getId() != null ? po.getId() : 0L);
        return ResultModel.ok(configs);
    }

    @RequestMapping(value = "/user_level/query_users")
    public ResultModel queryLevelUser(@RequestBody @Valid QueryLevelUserPO po, AdminUserReply adminUser){
        List<Long> users = userLevelService.queryLevelConfigUsers(adminUser.getOrgId(), po.getLevelConfigId(),
                po.getLastId(), po.getPageSize(), po.getQueryWhiteList());
        return ResultModel.ok(users);
    }

    @RequestMapping(value = "/user_level/add_white_list")
    public ResultModel addWhiteListUsers(@RequestBody @Valid UserLevelWhiteListPO po, AdminUserReply adminUser) {
        if (!CollectionUtils.isEmpty(po.getUserIds())) {
            List<Long> errorUserIds = getErrorUserIds(adminUser.getOrgId(), po.getUserIds());
            if (!CollectionUtils.isEmpty(errorUserIds)) {
                Map<String, Object> result = new HashMap<>();
                result.put("errorUserIds", errorUserIds);
                return ResultModel.error(ErrorCode.USER_IDS_ERROR.getCode(), "", result);
            }
        }
        userLevelService.addWhiteListUsers(adminUser.getOrgId(), po.getLevelConfigId(), po.getUserIds());
        return ResultModel.ok();
    }

    @RequestMapping(value = {"/brokeruser/user_level_info", "/user_level/user_level_info"})
    public ResultModel queryUserLevelInfo(@RequestBody @Valid IdPO po, AdminUserReply adminUser){
        UserLevelInfoDTO result = userLevelService.getUserLevelInfo(adminUser.getOrgId(), po.getId());
        return ResultModel.ok(result);
    }


    @RequestMapping(value = "/user_level/add_have_token_discount")
    public ResultModel addHaveTokenDiscount(@RequestBody @Valid UserLevelHaveTokenDiscountDTO po, AdminUserReply adminUser) {
        if (StringUtils.isEmpty(po.getTokenId())) {
            return ResultModel.ok();
        }
        userLevelService.addHaveTokenDiscount(adminUser.getOrgId(), po, adminUser);
        return ResultModel.ok();
    }

  
    @RequestMapping(value = "/user_level/query_have_token_discount")
    public ResultModel queryHaveTokenDiscount(AdminUserReply adminUser) {
        UserLevelHaveTokenDiscountDTO dto = userLevelService.queryHaveTokenDiscount(adminUser.getOrgId());
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/user_level/query_margin_interest" , method = RequestMethod.GET)
    public ResultModel queryMarginInterest(@RequestParam(name = "levelConfigId",required = false, defaultValue = "0") Long levelConfigId){
        List<InterestConfigDTO> dto = marginService.queryMarginInterestByLevel(getOrgId(),levelConfigId);
        return ResultModel.ok(dto);
    }

}
