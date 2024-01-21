package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.constants.OpTypeConstant;
import io.bhex.broker.admin.controller.dto.AskWalletAddressDTO;
import io.bhex.broker.admin.controller.dto.DepositDTO;
import io.bhex.broker.admin.controller.dto.WithdrawOrderDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.service.DepositService;
import io.bhex.broker.admin.service.WithdrawOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/withdraw_order")
public class WithdrawOrderController extends BrokerBaseController {

    @Autowired
    private WithdrawOrderService withdrawOrderService;

    @Autowired
    private DepositService depositService;

    @GetMapping({"/ask_wallet_address", "/query_ask_wallet_address"})
    public ResultModel queryAskWalletAddress(@RequestParam String tokenId,
                                             @RequestParam String address,
                                             @RequestParam(required = false, defaultValue = "") String memo) {

        AdminUserReply requestUser = getRequestUser();

        AskWalletAddressDTO dto = withdrawOrderService.askWalletAddress(tokenId, address, memo, requestUser.getOrgId());
        if (dto == null) {
            return ResultModel.error("ask_wallet error");
        }
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/unverified_list", method = RequestMethod.POST)
    public ResultModel queryWithdrawUnverifiedList(@RequestBody @Valid WithdrawOrderListPO po) {
        AdminUserReply requestUser = getRequestUser();

        Long requestAccountId = 0L;
        if ((po.getUserId() != null && po.getUserId() > 0) || StringUtils.isNotEmpty(po.getMobile()) || StringUtils.isNotEmpty(po.getEmail())) {
            Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, requestUser.getOrgId());
            if (combo2 == null) {
                return ResultModel.ok(new ArrayList<>());
            }
            requestAccountId = combo2.getV2();
        }


        List<WithdrawOrderUnverifyListRes> list = withdrawOrderService
                .queryUnverfiedOrders(requestUser.getOrgId(),
                        requestAccountId,
                        po.getNext() ? 0 : po.getFromId(),
                        po.getNext() ? po.getLastId() : 0,
                        po.getPageSize());
        //log.info("accountId:{} list:{}", accountId, list);
        return ResultModel.ok(list);
    }

    @BussinessLogAnnotation(opContent = "Review WithdrawalOrder(UID:{#po.userId} OrderId:{#po.withdrawOrderId}) {#po.verifyPassed ? 'Passed' : 'Rejected'}",
            entityId = "{#po.withdrawOrderId}", subType = "{#po.verifyPassed ? 'Passed' : 'Rejected'}",
            name = OpTypeConstant.WITHDRAW_ORDER_VERIFY)
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ResultModel withdrawOrderVerify(@RequestBody @Valid WithdrawOrderVerifyPO po) {
        AdminUserReply requestUser = getRequestUser();
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, requestUser.getOrgId());

        ResultModel resultModel = withdrawOrderService.verify(requestUser.getOrgId(), combo2.getV1(), combo2.getV2(), po.getWithdrawOrderId(),
                po.getVerifyPassed(), po.getRemark(), requestUser.getUsername(), po.getFailedReason(), po.getRefuseReason());


        return resultModel;
    }

    @RequestMapping(value = "/verify_history", method = RequestMethod.POST)
    public ResultModel queryWithdrawHistory(@RequestBody @Valid WithdrawOrderListPO po, AdminUserReply requestUser) {
        Long requestAccountId = 0L;
        if ((po.getUserId() != null && po.getUserId() > 0) || StringUtils.isNotEmpty(po.getMobile()) || StringUtils.isNotEmpty(po.getEmail())) {
            Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, requestUser.getOrgId());
            if (combo2 == null) {
                return ResultModel.ok(new ArrayList<>());
            }
            requestAccountId = combo2.getV2();
        }

        List<WithdrawOrderVerifyListRes> list = withdrawOrderService
                .queryVerfiedOrders(requestUser.getOrgId(), requestAccountId,
                        po.getNext() ? 0 : po.getFromId(),
                        po.getNext() ? po.getLastId() : 0,
                        po.getPageSize());

        return ResultModel.ok(list);
    }

    @RequestMapping(value = {"/query/withdraw", "/query"}, method = RequestMethod.POST)
    public ResultModel queryWithdrawOrder(@RequestBody @Valid WithdrawOrderListPO po, AdminUserReply adminUser) {

        long orgId = adminUser.getOrgId();
        Long accountId = 0L;
        if (hasUserQueryCondition(po)) {
            Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, adminUser.getOrgId());
            if (combo2 == null) {
                return ResultModel.ok(new ArrayList<>());
            }
            accountId = combo2.getV2();
        }

        List<WithdrawOrderDTO> list = withdrawOrderService.queryWithdrawOrder(orgId, accountId, po.getFromId(), po.getLastId(),
                po.getPageSize(), po.getTokenId(), po.getStartTime(), po.getEndTime(), po.getAddress(), po.getTxId());

        return ResultModel.ok(list);
    }

    private GetBrokerUserPO getGetBrokerUserPO(WithdrawOrderListPO param) {
        GetBrokerUserPO po = new GetBrokerUserPO();
        po.setEmail(param.getEmail());
        po.setPhone(param.getMobile());
        po.setNationalCode(param.getNationalCode());
        po.setUserId(param.getUserId());
        return po;
    }


    @RequestMapping(value = "/query/deposit", method = RequestMethod.POST)
    public ResultModel queryDepositOrder(@RequestBody @Valid DepositOrderListPO po, AdminUserReply requestUser) {
        //log.info("request info : {}", po);
        Long userId = null;

        long orgId = requestUser.getOrgId();
        // get userId id
        if ((po.getUserId() != null && po.getUserId() > 0)
                || StringUtils.isNotEmpty(po.getMobile()) || StringUtils.isNotEmpty(po.getEmail())) {
            Combo2<Long, Long> combo2 = getUserIdAndAccountId(getGetBrokerUserPO(po), orgId);
            if (combo2 != null) {
                userId = combo2.getV1();
            }
            if (null == userId) {
                return ResultModel.ok(new ArrayList());
            }
        }

        List<DepositDTO> list = depositService.queryDepositOrders(orgId, userId, po.getFromId(), po.getLastId(), po.getPageSize(),
                po.getTokenId(), po.getStartTime(), po.getEndTime(), po.getAddress(), po.getTxId());
        //log.info("userId:{} list:{}", userId, list);

        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/query/deposit/un_receipt", method = RequestMethod.POST)
    public ResultModel queryUnReceipt(@RequestBody @Valid DepositOrderListPO po) {
        // log.info("request info : {}", po);
        AdminUserReply requestUser = getRequestUser();
        Long userId = null;
        Long accountId = null;

        long orgId = requestUser.getOrgId();
        // get userId id
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(getGetBrokerUserPO(po), orgId);
        if (combo2 != null) {
            userId = combo2.getV1();
            accountId = combo2.getV2();
        }

//        if (null == userId || null == accountId) {
//            return ResultModel.ok(new ArrayList());
//        }

        List<DepositDTO> list = depositService.queryUnReceipts(accountId, userId, po.getNext() ? 0 : po.getFromId(),
                po.getNext() ? po.getLastId() : 0, po.getPageSize(), po.getTokenId(),
                po.getStartTime(), po.getEndTime(), po.getTxId(), orgId);

        //log.info("queryUnReceipt userId:{} list:{}", userId, list);

        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/deposit/un_receipt/exec", method = RequestMethod.POST)
    public ResultModel execReceipt(@RequestBody @Valid DepositReceiptExecPO po) {
        AdminUserReply requestUser = getRequestUser();

        if (po.getExecReceipt()) {
            return depositService.execReceipt(requestUser.getOrgId(), po.getAccountId(), po.getOrderId());
        }

        return ResultModel.ok();
    }

    private GetBrokerUserPO getGetBrokerUserPO(DepositOrderListPO param) {
        GetBrokerUserPO po = new GetBrokerUserPO();
        po.setEmail(param.getEmail());
        po.setPhone(param.getMobile());
        po.setNationalCode(param.getNationalCode());
        po.setUserId(param.getUserId());
        return po;
    }
}
