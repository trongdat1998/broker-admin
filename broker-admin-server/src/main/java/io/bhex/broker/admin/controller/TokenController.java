package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.token.TokenDetail;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.bhop.common.util.validation.ValidUtil;
import io.bhex.broker.admin.controller.dto.SimpleTokenDTO;
import io.bhex.broker.admin.controller.dto.TokenDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.service.TokenService;
import io.bhex.broker.grpc.common.AdminSimplyReply;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 9:13 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping(value = "/api/v1/token")
public class TokenController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ResultModel queryToken(@RequestBody @Valid QueryTokenPO query, AdminUserReply adminUser) {
        if (StringUtils.isNotEmpty(query.getTokenName()) && !ValidUtil.isTokenName(query.getTokenName().toUpperCase())) {
            return ResultModel.ok();
        }

        long orgId = adminUser.getOrgId();
        //tokenService.syncBhexTokens(orgId);
        PaginationVO<TokenDTO> vo = tokenService.queryToken(query.getCurrent(), query.getPageSize(), query.getCategory(),
                query.getTokenName(), orgId, query.getExtraRequestInfos());
        return ResultModel.ok(vo);
    }

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/query_simple", method = RequestMethod.POST)
    public ResultModel querySimpleTokens(@RequestBody @Valid QuerySimpleTokenPO query, AdminUserReply adminUser) {
        long orgId = adminUser.getOrgId();
        List<SimpleTokenDTO> list = tokenService.querySimpleTokens(orgId, query.getCategory());
        return ResultModel.ok(list);
    }

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/query_quote_tokens", method = RequestMethod.POST)
    public ResultModel queryMyQuoteTokens(AdminUserReply adminUser) {
        List<String> vo = tokenService.queryQuoteTokens(adminUser.getOrgId());
        return ResultModel.ok(vo);
    }

    @BussinessLogAnnotation(opContent = "open token:{#po.tokenId} deposit")
    @RequestMapping(value = "/allow_deposit", method = RequestMethod.POST)
    public ResultModel allowDeposit(@RequestBody @Valid ChangeTokenStatusPO po) {
        TokenDetail tokenDetail = tokenService.getTokenFromBh(po.getTokenId(), getOrgId());
        if (!tokenDetail.getAllowDeposit()) {
            return ResultModel.error("bh.closed.token.deposit");
        }

        Boolean isOk = tokenService.allowDeposit(po.getTokenId(), Boolean.TRUE, getOrgId());
        return isOk ? ResultModel.ok() : ResultModel.error("not.allowed!");
    }

    @BussinessLogAnnotation(opContent = "close token:{#po.tokenId} deposit")
    @RequestMapping(value = "/forbid_deposit", method = RequestMethod.POST)
    public ResultModel forbidDeposit(@RequestBody @Valid ChangeTokenStatusPO po) {
        Boolean isOk = tokenService.allowDeposit(po.getTokenId(), Boolean.FALSE, getOrgId());
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "open token:{#po.tokenId} withdrawal")
    @RequestMapping(value = "/allow_withdraw", method = RequestMethod.POST)
    public ResultModel allowWithdraw(@RequestBody @Valid ChangeTokenStatusPO po) {
        TokenDetail tokenDetail = tokenService.getTokenFromBh(po.getTokenId(), getOrgId());
        if (!tokenDetail.getAllowWithdraw()) {
            return ResultModel.error("bh.closed.token.withdraw");
        }

        Boolean isOk = tokenService.allowWithdraw(po.getTokenId(), Boolean.TRUE, getOrgId());
        return isOk ? ResultModel.ok() : ResultModel.error("not.allowed!");
    }

    @BussinessLogAnnotation(opContent = "close token:{#po.tokenId} withdrawal")
    @RequestMapping(value = "/forbid_withdraw", method = RequestMethod.POST)
    public ResultModel forbidWithdraw(@RequestBody @Valid ChangeTokenStatusPO po) {
        Boolean isOk = tokenService.allowWithdraw(po.getTokenId(), Boolean.FALSE, getOrgId());
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "token:{#po.tokenId} visible")
    @RequestMapping(value = "/allow_publish", method = RequestMethod.POST)
    public ResultModel publish(@RequestBody @Valid ChangeTokenStatusPO po) {
        Boolean isOk = tokenService.publish(po.getTokenId(), Boolean.TRUE, getOrgId());
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "token:{#po.tokenId} unvisible")
    @RequestMapping(value = "/forbid_publish", method = RequestMethod.POST)
    public ResultModel forbidPublish(@RequestBody @Valid ChangeTokenStatusPO po) {
        Boolean isOk = tokenService.publish(po.getTokenId(), Boolean.FALSE, getOrgId());
        return ResultModel.ok(isOk);
    }

    @RequestMapping(value = "/test_sync", method = RequestMethod.POST)
    public ResultModel test() {
        tokenService.syncBhexTokens(getOrgId());
        return ResultModel.ok();
    }

    @RequestMapping(value = "/set_high_risk_token", method = RequestMethod.POST)
    public ResultModel setHighRiskToken(@RequestBody Map<String, Object> paramMap) {
        String tokenId = paramMap.getOrDefault("tokenId", "").toString();
        if (Strings.isNullOrEmpty(tokenId)) {
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        Boolean isHighRiskToken = Boolean.valueOf(paramMap.getOrDefault("isHighRiskToken", "false").toString());
        boolean r = tokenService.setHighRiskToken(getOrgId(), tokenId, isHighRiskToken);
        return ResultModel.ok(r);
    }

    @RequestMapping(value = "/set_token_withdraw_fee", method = RequestMethod.POST)
    public ResultModel setTokenWithdrawFee(@RequestBody @Valid TokenWithdrawFee fee) {
        boolean r = tokenService.setWithdrawFee(getOrgId(), fee.getTokenId(), fee.getFee());
        return ResultModel.ok(r);
    }

    @RequestMapping(value = "/edit_token_name", method = RequestMethod.POST)
    public ResultModel editTokenName(@RequestBody @Valid EditTokenNamePO po) {
        if (!ValidUtil.isTokenName(po.getTokenName().toUpperCase())) {
            return ResultModel.error("request.parameter.error");
        }
        AdminSimplyReply r = tokenService.editTokenName(getOrgId(), po.getTokenId(), po.getTokenName().toUpperCase());
        if (r.getResult()) {
            return ResultModel.ok();
        } else {
            return ResultModel.error(r.getMessage());
        }
    }

    @RequestMapping(value = "/edit_token_full_name", method = RequestMethod.POST)
    public ResultModel editTokenFullName(@RequestBody @Valid EditTokenFullNamePO po) {
        AdminSimplyReply r = tokenService.editTokenFullName(getOrgId(), po.getTokenId(), po.getTokenFullName());
        if (r.getResult()) {
            return ResultModel.ok();
        } else {
            return ResultModel.error(r.getMessage());
        }
    }

    @RequestMapping(value = "/edit_token_tag", method = RequestMethod.POST)
    public ResultModel editTokenExtraTag(@RequestBody @Valid EditTokenExtraTagPO po, AdminUserReply adminUser) {
        AdminSimplyReply r = tokenService.editTokenExtraTags(adminUser.getOrgId(), po.getTokenId(), po.getTags());
        if (r.getResult()) {
            return ResultModel.ok();
        } else {
            return ResultModel.error(r.getMessage());
        }
    }

    @RequestMapping(value = "/edit_token_config", method = RequestMethod.POST)
    public ResultModel editTokenExtraConfig(@RequestBody @Valid EditTokenExtraConfigPO po, AdminUserReply adminUser) {
        AdminSimplyReply r = tokenService.editTokenExtraConfigs(adminUser.getOrgId(), po.getTokenId(), po.getConfigs());
        if (r.getResult()) {
            return ResultModel.ok();
        } else {
            return ResultModel.error(r.getMessage());
        }
    }

    @RequestMapping(value = "/task/edit_config", method = RequestMethod.POST)
    public ResultModel editTokenTaskConfig(@RequestBody @Valid BrokerTokenTaskConfigDTO po) {

        if (po.getStatus() == 1 && po.getActionTime() < System.currentTimeMillis() + 60_000) {
            return ResultModel.error("request.parameter.error");
        }

        AdminUserReply adminUserReply = getRequestUser();
        long brokerId = adminUserReply.getOrgId();
        po.setOrgId(brokerId);

        TokenDetail tokenDetail = tokenService.getTokenFromBh(po.getTokenId(), brokerId);

        if (po.getDepositStatus() && !tokenDetail.getAllowDeposit()){
            return ResultModel.error("bh.closed.token.deposit");
        }

        if (po.getWithdrawStatus() && !tokenDetail.getAllowWithdraw()){
            return ResultModel.error("bh.closed.token.withdraw");
        }

        tokenService.editTokenTaskConfig(brokerId, adminUserReply, po);
        return ResultModel.ok();
    }

    @RequestMapping(value = "/task/query_configs", method = RequestMethod.POST)
    public ResultModel<String> queryTokenTaskConfigs(@RequestBody @Valid QueryTokenTasksPO po) {
        return ResultModel.ok(tokenService.getTokenTaskConfigs(getOrgId(), po.getPageSize(), po.getFromId()));
    }



}
