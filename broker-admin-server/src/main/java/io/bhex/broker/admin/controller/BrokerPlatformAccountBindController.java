package io.bhex.broker.admin.controller;

import io.bhex.base.account.AccountType;
import io.bhex.base.account.BindAccountReply;
import io.bhex.base.account.ExchangeReply;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.config.OrgInstanceConfig;
import io.bhex.bhop.common.dto.param.BalanceDetailDTO;
import io.bhex.bhop.common.dto.param.PlatformAccountAssetsPO;
import io.bhex.bhop.common.grpc.client.AccountAssetClient;
import io.bhex.bhop.common.grpc.client.BhAccountClient;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.FundAccountDTO;
import io.bhex.broker.admin.controller.param.AccountIdPO;
import io.bhex.broker.admin.controller.param.BindFundAccountCheckPO;
import io.bhex.broker.admin.controller.param.BindFundAccountPO;
import io.bhex.broker.admin.controller.param.BrokerPlatformAccountBindPO;
import io.bhex.broker.admin.grpc.client.impl.OrgClient;
import io.bhex.broker.admin.service.PlatformAccountBindService;
import io.bhex.broker.admin.service.impl.PlatformAccountBindServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Date: 2018/10/10 下午6:41
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/platform_account")
public class BrokerPlatformAccountBindController extends BrokerBaseController {

    @Autowired
    private AccountAssetClient accountAssetClient;
    @Autowired
    private BhAccountClient bhAccountClient;
    @Autowired
    private PlatformAccountBindService platformAccountBindService;

    @Autowired
    private PlatformAccountBindServiceImpl platformAccountBindServiceImpl;

    @Autowired
    private OrgInstanceConfig orgInstanceConfig;

    @Autowired
    private OrgClient orgClient;

    private boolean checkAccountType(int accountType) {
        if (accountType != AccountType.BROKER_REVENUE_ACCOUNT_VALUE
                && accountType != AccountType.BROKER_EARNEST_ACCOUNT_VALUE
                && accountType != AccountType.OPERATION_ACCOUNT_VALUE) {
            return false;
        }
        return true;
    }


    @RequestMapping(value = "/assets", method = RequestMethod.POST)
    public ResultModel<Map<String, Object>> getAssets(@RequestBody @Valid PlatformAccountAssetsPO po) {
        if (!checkAccountType(po.getAccountType())) {
            return ResultModel.error("request.parameter.error");
        }
        Map<String, Object> result = new HashMap<>();
        Long accountId = bhAccountClient.bindRelation(getOrgId(), AccountType.forNumber(po.getAccountType()));
        if (accountId == null || accountId == 0) {
            result.put("isBind", false);
            return ResultModel.ok(result);
        }

        Long brokerId = bhAccountClient.getAccountBrokerId(accountId, getOrgId());
        result.put("accountId", String.valueOf(accountId));
        result.put("brokerName", orgInstanceConfig.getBrokerInstance(brokerId).getBrokerName());
        List<BalanceDetailDTO> details = accountAssetClient.getBalances(brokerId, accountId);
//        if (CollectionUtils.isEmpty(details)) {
//            return ResultModel.ok(new ArrayList<>());
//        }
//        details = details.stream()
//                .collect(Collectors.toList());

        result.put("isBind", true);
        result.put("list", details);
        return ResultModel.ok(result);
    }


    @BussinessLogAnnotation
    @RequestMapping(value = "/send_validate_code", method = RequestMethod.POST)
    public ResultModel sendBindAccountValidateCode(@RequestBody @Valid BrokerPlatformAccountBindPO po) {

        if (!checkAccountType(po.getAccountType())) {
            return ResultModel.error("request.parameter.error");
        }

        PlatformAccountBindService.InnerResult result = platformAccountBindService.checkBindInput(getOrgId(),
                po.getBrokerName(), po.getUsername(), po.getAccountType());
        if (!StringUtils.isEmpty(result.getErrorMsg())) {
            return ResultModel.error(result.getErrorMsg());
        }

        platformAccountBindService.sendVerifyCode(getOrgId(), po.getUsername(), po.getAccountType(), result);
        return ResultModel.ok();
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/bind", method = RequestMethod.POST)
    public ResultModel bindAccount(@RequestBody @Valid BrokerPlatformAccountBindPO po, AdminUserReply adminUser) {
        if (!checkAccountType(po.getAccountType())) {
            return ResultModel.error("request.parameter.error");
        }
        if (StringUtils.isEmpty(po.getValidateCode())) {
            return ResultModel.errorParameter("validateCode", "verify.code.error");
        }

        PlatformAccountBindService.InnerResult result = platformAccountBindService.checkBindInput(getOrgId(),
                po.getBrokerName(), po.getUsername(), po.getAccountType());
        if (!StringUtils.isEmpty(result.getErrorMsg())) {
            return ResultModel.error(result.getErrorMsg());
        }

        PlatformAccountBindService.InnerResult verifyCodeResult = platformAccountBindService.checkVerifyCode(getOrgId(),
                po.getValidateCode(), po.getAccountType(), result.getAccountId());
        if (!StringUtils.isEmpty(verifyCodeResult.getErrorMsg())) {
            return ResultModel.error(verifyCodeResult.getErrorMsg());
        }
        int exchangeAccountType = 0;
        if (po.getAccountType() == AccountType.BROKER_REVENUE_ACCOUNT_VALUE) {
            exchangeAccountType = AccountType.EXCHANGE_EARNEST_ACCOUNT_VALUE;
        } else if (po.getAccountType() == AccountType.OPERATION_ACCOUNT_VALUE) {
            exchangeAccountType = AccountType.OPERATION_ACCOUNT_VALUE;
        }
        if (exchangeAccountType > 0) {
            //判断是否绑定exchange对应的运营和营收账户
            ExchangeReply exchangeReply = orgClient.findTrustExchangeByBrokerId(adminUser.getOrgId());
            if (exchangeReply == null || exchangeReply.getExchangeId() == 0) {
                log.error("{} no trust exchange", adminUser.getOrgId());
                return ResultModel.error("request.parameter.error");
            }
            BindAccountReply reply = bhAccountClient.bindAccount(exchangeReply.getExchangeId(), result.getAccountId(),
                    AccountType.forNumber(exchangeAccountType));
            if (BindAccountReply.Result.OK.equals(reply.getResult()) || BindAccountReply.Result.BINDING.equals(reply.getResult())) {
                log.info("bind exchange account success!{}", AccountType.forNumber(exchangeAccountType).name());
            } else if (BindAccountReply.Result.ACCOUT_ID_ERROR.equals(reply.getResult())) {
                return ResultModel.error("platform.bind.account.failed");
            } else {
                return ResultModel.error("platform.bind.account.failed");
            }
        }
        BindAccountReply reply = bhAccountClient.bindAccount(getOrgId(), result.getAccountId(),
                AccountType.forNumber(po.getAccountType()));
        BindAccountReply.Result r = reply.getResult();
        log.info("bindAccount:{} result:{}", po, r);
        if (r.equals(BindAccountReply.Result.OK)) {
            return ResultModel.ok();
        }
        if (r.equals(BindAccountReply.Result.BINDING)) {
            return ResultModel.error("platform_account.binding");
        }
        if (r.equals(BindAccountReply.Result.ACCOUT_ID_ERROR)) {
            return ResultModel.error("platform_account.accountid.error");
        }
        return ResultModel.error("platform.bind.account.failed");
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/fund_account/check", method = RequestMethod.POST)
    public ResultModel checkBindFundAccount(@RequestBody @Valid BindFundAccountCheckPO po) {

        Long orgId = getOrgId();
        return platformAccountBindServiceImpl.checkBindFundAccount(orgId, po.getAccountId());
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/fund_account/bind", method = RequestMethod.POST)
    public ResultModel bindFundAccount(@RequestBody @Valid BindFundAccountPO po) {

        Long orgId = getOrgId();
        return platformAccountBindServiceImpl.bindFundAccount(orgId, po);
    }

    @RequestMapping(value = "/fund_account/query", method = RequestMethod.POST)
    public ResultModel<FundAccountDTO> queryFundAccount() {

        Long orgId = getOrgId();
        List<FundAccountDTO> fundAccountDTOList = platformAccountBindServiceImpl.queryFundAccount(orgId);

        return ResultModel.ok(fundAccountDTOList);
    }

    @RequestMapping(value = "/fund_account/set_visible", method = RequestMethod.POST)
    public ResultModel<FundAccountDTO> setFundAccountVisible(@RequestBody @Valid AccountIdPO po) {

        Long orgId = getOrgId();
        platformAccountBindServiceImpl.setFundAccountVisible(orgId, po.getAccountId());
        return ResultModel.ok();
    }

    @RequestMapping(value = "/fund_account/set_unvisible", method = RequestMethod.POST)
    public ResultModel<FundAccountDTO> setFundAccountUnVisible(@RequestBody @Valid AccountIdPO po) {

        Long orgId = getOrgId();
        platformAccountBindServiceImpl.setFundAccountUnVisible(orgId, po.getAccountId());
        return ResultModel.ok();
    }

//    @RequestMapping(value = "/bind", method = RequestMethod.POST)
//    public ResultModel bindAccount(@RequestBody @Valid PlatformAccountBindAccountPO po){
//        if(!checkAccountType(po.getAccountType())){
//            return ResultModel.error("request.parameter.error");
//        }
//
//        Optional<BrokerInstanceRes> optional = orgInstanceConfig.listBrokerInstances().stream()
//                .filter(b->b.getBrokerName().equals(po.getBrokerName()))
//                .findFirst();
//        if(!optional.isPresent()){
//            return ResultModel.error("platform_account.broker.name.not.found");
//        }
//
//        //TODO check accountid
//
//
//        BindAccountReply reply = bhAccountClient.bindAccount(getOrgId(), po.getAccountId(),
//                AccountType.forNumber(po.getAccountType()));
//        BindAccountReply.Result r = reply.getResult();
//        log.info("bindAccount:{} result:{}", po, r);
//        if(r.equals(BindAccountReply.Result.OK)){
//            return ResultModel.ok();
//        }
//        if(r.equals(BindAccountReply.Result.BINDING)){
//            return ResultModel.error("platform_account.binding");
//        }
//        if(r.equals(BindAccountReply.Result.ACCOUT_ID_ERROR)){
//            return ResultModel.error("platform_account.accountid.error");
//        }
//        return ResultModel.error("platform.bind.account.failed");
//    }
}
