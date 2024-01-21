package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.RedPacketDTO;
import io.bhex.broker.admin.controller.dto.RedPacketReceiveDetailDTO;
import io.bhex.broker.admin.model.RedPacketTheme;
import io.bhex.broker.admin.model.RedPacketTokenConfig;
import io.bhex.broker.admin.service.impl.RedPacketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/red_packet")
public class RedPacketController {

    @Resource
    private RedPacketService redPacketService;

    /*
     * openRedPacketFunction放在了功能管理，如果券商打开了red_packet，那么broker-server就直接执行
     */

    @RequestMapping(value = "/themes")
    public ResultModel<List<RedPacketTheme>> queryRedPacketTheme(AdminUserReply adminUser) {
        Map<Integer, List<RedPacketTheme>> themeTypeMap = redPacketService.queryRedPacketTheme(adminUser.getOrgId());
        return ResultModel.ok(themeTypeMap);
    }

    @RequestMapping(value = "/token_configs")
    public ResultModel<List<RedPacketTokenConfig>> queryRedPacketTokenConfig(AdminUserReply adminUser) {
        List<RedPacketTokenConfig> tokenConfigList = redPacketService.queryRedPacketTokenConfig(adminUser.getOrgId());
        return ResultModel.ok(tokenConfigList);
    }

    @PostMapping(value = "/theme")
    public ResultModel saveRedPacketTheme(@RequestBody List<RedPacketTheme> themes, AdminUserReply adminUser) {
        redPacketService.saveRedPacketThemes(adminUser.getOrgId(), themes);
        return ResultModel.ok();
    }

    @PostMapping(value = "/token_config")
    public ResultModel saveRedPacketTokenConfig(@RequestBody RedPacketTokenConfig tokenConfig, AdminUserReply adminUser) {
        redPacketService.saveRedPacketTokenConfig(adminUser.getOrgId(), tokenConfig);
        return ResultModel.ok();
    }

    @PostMapping(value = "/change_theme_custom_order")
    public ResultModel changeThemeCustomOrder(@RequestBody Map<Long, Integer> customOrderMap, AdminUserReply adminUser) {
        redPacketService.changeCustomOrder(adminUser.getOrgId(), "theme", customOrderMap);
        return ResultModel.ok();
    }

    @PostMapping(value = "/change_token_config_custom_order")
    public ResultModel changeTokenConfigCustomOrder(@RequestBody List<Long> tokenConfigIds, AdminUserReply adminUser) {
        if (tokenConfigIds == null || tokenConfigIds.size() == 0) {
            return ResultModel.ok();
        }
        Map<Long, Integer> customOrderMap = Maps.newHashMap();
        int maxIndex = tokenConfigIds.size();
        for (Long tokenConfigId : tokenConfigIds) {
            customOrderMap.put(tokenConfigId, maxIndex--);
        }
        redPacketService.changeCustomOrder(adminUser.getOrgId(), "tokenConfig", customOrderMap);
        return ResultModel.ok();
    }

    @RequestMapping(value = "/red_packets")
    public ResultModel<List<RedPacketDTO>> queryRedPacketList(@RequestBody(required = false) Map<String, Object> paramMap, AdminUserReply adminUser) {
        if (paramMap == null) {
            paramMap = Maps.newHashMap();
        }
        String userIdStr = paramMap.getOrDefault("userId", "0").toString();
        Long userId = Strings.isNullOrEmpty(userIdStr) ? 0 : Long.valueOf(userIdStr);
        Long fromId = Long.valueOf(paramMap.getOrDefault("fromId", "0").toString());
        Integer limit = Integer.valueOf(paramMap.getOrDefault("limit", "100").toString());
        List<RedPacketDTO> redPacketList = redPacketService.queryRedPacketList(adminUser.getOrgId(), userId, fromId, limit);
        return ResultModel.ok(redPacketList);
    }

    @RequestMapping(value = "/red_packet_receive_details")
    public ResultModel<List<RedPacketReceiveDetailDTO>> queryRedPacketReceiveDetail(@RequestBody(required = false) Map<String, Object> paramMap, AdminUserReply adminUser) {
        if (paramMap == null) {
            paramMap = Maps.newHashMap();
        }
        String redPacketIdStr = paramMap.getOrDefault("redPacketId", "0").toString();
        Long redPacketId = Strings.isNullOrEmpty(redPacketIdStr) ? 0 : Long.valueOf(redPacketIdStr);
        String userIdStr = paramMap.getOrDefault("userId", "0").toString();
        Long userId = Strings.isNullOrEmpty(userIdStr) ? 0 : Long.valueOf(userIdStr);
        Long fromId = Long.valueOf(paramMap.getOrDefault("fromId", "0").toString());
        Integer limit = Integer.valueOf(paramMap.getOrDefault("limit", "100").toString());
        List<RedPacketReceiveDetailDTO> redPacketReceiveDetailList = redPacketService.queryRedPacketReceiveDetailList(adminUser.getOrgId(), redPacketId, userId, fromId, limit);
        return ResultModel.ok(redPacketReceiveDetailList);
    }

}
