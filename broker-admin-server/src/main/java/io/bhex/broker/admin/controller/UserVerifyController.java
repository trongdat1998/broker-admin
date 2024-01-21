package io.bhex.broker.admin.controller;

import com.alibaba.fastjson.JSON;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.constants.OpTypeConstant;
import io.bhex.broker.admin.controller.dto.UserVerifyDTO;
import io.bhex.broker.admin.controller.dto.UserVerifyHistoryDTO;
import io.bhex.broker.admin.controller.dto.UserVerifyReasonDTO;
import io.bhex.broker.admin.controller.param.QueryUserVerifyPO;
import io.bhex.broker.admin.controller.param.SimpleBrokerUserPO;
import io.bhex.broker.admin.controller.param.UserVerifyIdPO;
import io.bhex.broker.admin.controller.param.VerifyUserPO;
import io.bhex.broker.admin.service.UserVerifyService;
import io.bhex.broker.grpc.admin.DegradeBrokerKycLevelReply;
import io.bhex.broker.grpc.admin.OpenThirdKycAuthReply;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 24/08/2018 5:11 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1")
public class UserVerifyController extends BrokerBaseController {

    @Autowired
    private UserVerifyService userVerifyService;

    //service for kycall
    @RequestMapping(value = "/all_user_verify/query", method = RequestMethod.POST)
    public ResultModel queryAllUserVerify(@RequestBody @Valid QueryUserVerifyPO query, Locale locale, AdminUserReply adminUser) {
        Long brokerId = adminUser.getOrgId();
        Long userId = query.getUserId();
        //查询指定用户
        boolean userQuery = (userId != null && userId > 0)
                || !StringUtils.isEmpty(query.getEmail())
                || !StringUtils.isEmpty(query.getPhone());
        if (userQuery) {
            Combo2<Long, Long> combo2 =  getUserIdAndAccountId(query, brokerId);
            if (combo2 == null) {
                PaginationVO<UserVerifyDTO> vo = new PaginationVO<>();
                vo.setCurrent(query.getCurrent());
                vo.setTotal(0);
                vo.setPageSize(query.getPageSize());
                return ResultModel.ok();
            }
            userId = combo2.getV1();
            query.setUserId(userId);
        }


        PaginationVO<UserVerifyDTO> vo = userVerifyService.queryUserVerifyList(query,
                LocaleUtil.getLanguage(locale), brokerId);
        return ResultModel.ok(vo);
    }

    @RequestMapping(value = "/user_verify/query", method = RequestMethod.POST)
    public ResultModel queryUnVerifiedList(@RequestBody @Valid QueryUserVerifyPO query, Locale locale) {
        Long brokerId = getOrgId();
        Long userId = query.getUserId();
        //查询指定用户
        boolean userQuery = (userId != null && userId > 0)
                || !StringUtils.isEmpty(query.getEmail())
                || !StringUtils.isEmpty(query.getPhone());
        if (userQuery) {
            Combo2<Long, Long> combo2 =  getUserIdAndAccountId(query, brokerId);
            if (combo2 == null) {
                PaginationVO<UserVerifyDTO> vo = new PaginationVO<>();
                vo.setCurrent(query.getCurrent());
                vo.setTotal(0);
                vo.setPageSize(query.getPageSize());
                return ResultModel.ok();
            }
            userId = combo2.getV1();
            query.setUserId(userId);
        }


        PaginationVO<UserVerifyDTO> vo = userVerifyService.queryUnverifyiedUser(query,
                LocaleUtil.getLanguage(locale), brokerId);
        return ResultModel.ok(vo);
    }

    @RequestMapping(value = "/user_verify/show", method = RequestMethod.POST)
    public ResultModel queryUserVerify(@RequestBody @Valid UserVerifyIdPO query, Locale locale) {
        long brokerId = getOrgId();
        UserVerifyDTO vo = userVerifyService.getVerifyUserById(query.getUserVerifyId(), brokerId, LocaleUtil.getLanguage(locale), true);
        if (vo.getUpdated() == 0) {
            vo.setUpdated(vo.getCreated());
        }
        return ResultModel.ok(vo);
    }


    @RequestMapping(value = "/user_verify/verification", method = RequestMethod.POST)
    public ResultModel updateVerifyUser(@RequestBody @Valid VerifyUserPO po) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        Boolean isOk = userVerifyService.updateVerifyUser(brokerId, requestUser.getId(), po.getUserVerifyId(),
                po.getVerifyPassed() ? VerifyUserPO.UserVerifyStatus.PASSED.value() : VerifyUserPO.UserVerifyStatus.REFUSED.value(),
                po.getReasonId(), po.getRemark());

        UserVerifyDTO vo = userVerifyService.getVerifyUserById(po.getUserVerifyId(), brokerId, LocaleUtil.getLanguage(), false);

        String subType = po.getVerifyPassed() ? "passed" : "rejected";
        String opContent = "UID:" + vo.getUserId() + " kyc was " + subType;
        saveBizLog(OpTypeConstant.USER_KYC_VERIFY, subType, opContent, JSON.toJSONString(po), isOk ? 0 : 1, vo.getUserId().toString());
        return ResultModel.ok(isOk);
    }

    @RequestMapping(value = "/user_verify/verify_reason", method = RequestMethod.GET)
    public ResultModel listVerifyReason(Locale locale) {
        List<UserVerifyReasonDTO> userVerifyReasons = userVerifyService.listVerifyReason(LocaleUtil.getLanguage(locale));
        return ResultModel.ok(userVerifyReasons);
    }

    @RequestMapping(value = "/user_verify/verify_history", method = RequestMethod.POST)
    public ResultModel listVerifyHistory(@RequestBody @Valid UserVerifyIdPO po,
                                         Locale locale) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        List<UserVerifyHistoryDTO> result = userVerifyService.listVerifyHistory(brokerId, LocaleUtil.getLanguage(locale), po.getUserVerifyId());
        return ResultModel.ok(result);
    }

    @BussinessLogAnnotation(opContent = "kyclevel degrade:{#po.userId}", entityId = "{#po.userId}")
    @RequestMapping(value = "/user_verify/degrade_kyc_level", method = RequestMethod.POST)
    public ResultModel degradeKycLevel(@RequestBody @Valid SimpleBrokerUserPO po) {
        DegradeBrokerKycLevelReply reply = userVerifyService.degradeBrokerKycLevel(getOrgId(), po.getUserId());
        return ResultModel.ok();
    }

    @BussinessLogAnnotation(opContent = "open triple kyc:{#po.userId}", entityId = "{#po.userId}")
    @RequestMapping(value = "/user_verify/open_triple_kyc", method = RequestMethod.POST)
    public ResultModel openThirdKycAuth(@RequestBody @Valid SimpleBrokerUserPO po) {
        OpenThirdKycAuthReply reply = userVerifyService.openThirdKycAuth(getOrgId(), po.getUserId());
        return ResultModel.ok();
    }
}
