package io.bhex.broker.admin.controller;


import com.google.common.collect.Lists;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.service.AdminLoginUserService;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.ExperienceFundInfoDTO;
import io.bhex.broker.admin.controller.dto.ExperienceFundTransferRecordDTO;
import io.bhex.broker.admin.controller.param.IdPO;
import io.bhex.broker.admin.controller.param.QueryExperienceFundPO;
import io.bhex.broker.admin.controller.param.SaveExperienceFundPO;
import io.bhex.broker.admin.service.impl.ExperienceFundService;
import io.bhex.broker.admin.util.NumberUtil;
import io.bhex.broker.grpc.admin.UserAccountMap;
import io.bhex.broker.grpc.common.AccountTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/experience_fund")
public class ActivityExperienceFundController extends BrokerBaseController {

    @Autowired
    private AdminLoginUserService adminLoginUserService;
    @Autowired
    private ExperienceFundService experienceFundService;


    @RequestMapping(value = {"/create", "/check"}, method = RequestMethod.POST)
    public ResultModel createExperienceFundActivity(@RequestBody @Valid SaveExperienceFundPO po, AdminUserReply adminUser) {
        boolean submit = !StringUtils.isEmpty(po.getVerifyCode());
        if (submit) {
            adminLoginUserService.verifyAdvance(po.getAuthType(), po.getVerifyCode(), adminUser.getId(),
                    adminUser.getOrgId(), getAdminPlatform());
        }

        List<String> errorUserIds = new ArrayList<>();
        List<String> userIdList = Lists.newArrayList(po.getUserIds().split(",")).stream()
                .map(u -> getUserIdStr(u)).collect(Collectors.toList());
        for (String userId : userIdList) {
            if (!NumberUtil.isDigits(userId)) {
                errorUserIds.add(userId);
            }
        }
        List<Long> userIds = userIdList.stream()
                .filter(d -> NumberUtil.isDigits(d))
                .map(d -> Long.parseLong(d))
                .collect(Collectors.toList());
        errorUserIds.addAll(
                getErrorUserIds(adminUser.getOrgId(), userIds, AccountTypeEnum.FUTURE) //合约用户
                        .stream().map(s -> s + "")
                        .collect(Collectors.toList())
        );
        log.info("error userids : {}", errorUserIds);
        if (!CollectionUtils.isEmpty(errorUserIds)) {
            Map<String, Object> result = new HashMap<>();
            result.put("errorUserIds", errorUserIds);
            return ResultModel.error(ErrorCode.USER_IDS_ERROR.getCode(), "", result);
        }
        po.setUserIdList(userIdList);

        List<UserAccountMap> accountMaps = listUserAccount(adminUser.getOrgId(),
                userIdList.stream().map(u -> Long.parseLong(u)).collect(Collectors.toList()),
                AccountTypeEnum.FUTURE);
        ResultModel resultModel = experienceFundService.saveExperienceFundInfo(po, adminUser, accountMaps, submit);


        return resultModel;

    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ResultModel queryExperienceFundActivities(@RequestBody @Valid QueryExperienceFundPO po, AdminUserReply adminUser) {

        List<ExperienceFundInfoDTO> list = experienceFundService.queryExperienceFunds(po, adminUser.getOrgId());
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/query_records", method = RequestMethod.POST)
    public ResultModel queryExperienceFundTransferRecords(@RequestBody @Valid QueryExperienceFundPO po, AdminUserReply adminUser) {
        List<ExperienceFundTransferRecordDTO> list = experienceFundService.queryTransferRecords(po, adminUser.getOrgId());
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public ResultModel queryExperienceFundActivity(@RequestBody @Valid IdPO po, AdminUserReply adminUser) {

        ExperienceFundInfoDTO dto = experienceFundService.queryExperienceFundDetail(po.getId(), adminUser.getOrgId());
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/contract_tokens", method = RequestMethod.POST)
    public ResultModel queryContractTokens(AdminUserReply adminUser) {

        List<String> tokens = experienceFundService.queryFuturesCoinToken(adminUser.getOrgId());
        return ResultModel.ok(tokens);
    }
}
