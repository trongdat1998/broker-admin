package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.service.AdminLoginUserService;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.service.StakingService;
import io.bhex.broker.admin.service.impl.StakingServiceImpl;
import io.bhex.broker.common.exception.BrokerErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/staking")
public class StakingController extends BaseController {

    @Autowired
    StakingService stakingService;
    @Autowired
    StakingServiceImpl stakingServiceImpl;
    @Autowired
    AdminLoginUserService adminLoginUserService;

    @RequestMapping(value = "/product/save", method = RequestMethod.POST)
    public ResultModel<Long> saveProduct(@RequestBody @Valid StakingProductPO stakingProductPO) {

        Long orgId = getOrgId();
        stakingProductPO.setOrgId(orgId);

        return stakingService.saveProduct(stakingProductPO);
    }

    @RequestMapping(value = "/product/get")
    public ResultModel<StakingProductDTO> getProductDetail(@RequestBody @Valid IdPO po) {

        Long orgId = getOrgId();

        StakingProductDTO dto = stakingService.getProductDetail(orgId, po.getId());
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/product/list")
    public ResultModel<List<StakingProductProfileDTO>> getProductList(@RequestBody @Valid StakingProductListPO po) {

        Long orgId = getOrgId();
        String language = LocaleUtil.getLanguage();

        List<StakingProductProfileDTO> results = stakingService.getProductList(orgId, language, po);

        return ResultModel.ok(results);
    }

    @RequestMapping(value = "/product/online")
    public ResultModel<Boolean> onlineProduct(@RequestBody @Valid IdPO po) {
        Long orgId = getOrgId();

        boolean success = stakingService.onlineProduct(orgId, po.getId(), 1);
        if (success) {
            return ResultModel.ok();
        } else {
            return ResultModel.error("Project status not allow update");
        }
    }

    @RequestMapping(value = "/product/offline")
    public ResultModel<Boolean> offlineProduct(@RequestBody @Valid IdPO po) {
        Long orgId = getOrgId();

        boolean success = stakingService.onlineProduct(orgId, po.getId(), 0);
        if (success) {
            return ResultModel.ok();
        } else {
            return ResultModel.error("Project status not allow update");
        }
    }

    @RequestMapping(value = "/product/get_permission")
    public ResultModel<StakingProductPermissionDTO> getProductPermission() {
        Long orgId = getOrgId();

        StakingProductPermissionDTO dto = stakingService.getBrokerProductPermission(orgId);
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/product/rebate/undo_list")
    public ResultModel<List<StakingProductRebateDTO>> queryProductUndoRebate(@RequestBody @Valid QueryProductUndoRebatePO po) {
        Long orgId = getOrgId();

        String language = LocaleUtil.getLanguage();

        List<StakingProductRebateDTO> dtoList = stakingService.queryBrokerProductUndoRebate(orgId, language, po.getProductType());
        return ResultModel.ok(dtoList);
    }

    @RequestMapping(value = "/product/rebate/history_list")
    public ResultModel<List<StakingProductRebateDTO>> queryProductHistoryRebate(@RequestBody @Valid QueryProductHistoryRebatePO po) {
        Long orgId = getOrgId();

        String language = LocaleUtil.getLanguage();

        List<StakingProductRebateDTO> dtoList = stakingService.queryBrokerProductHistoryRebate(orgId, language, po.getProductType(), po.getPageNo(), po.getSize());
        return ResultModel.ok(dtoList);
    }

    @RequestMapping(value = "/product/rebate/transfer")
    public ResultModel<Integer> dividendTransfer(@RequestBody StakingProductRebatePO po) {
        Long orgId = getOrgId();
        Long adminId = getRequestUserId();

        adminLoginUserService.verifyAdvance(po.getAuthType(), po.getVerifyCode(), adminId, orgId, getAdminPlatform());

        int rtnCode = stakingService.dividendTransfer(orgId, po.getProductId(), po.getProductRebateId());
        if (rtnCode == BrokerErrorCode.SUCCESS.code()) {
            return ResultModel.ok(rtnCode);
        } else {
            return ResultModel.error(BrokerErrorCode.fromCode(rtnCode).msg());
        }
    }

    @RequestMapping(value = "/product/rebate/cancel")
    public ResultModel<Boolean> cancelDividend(@RequestBody StakingProductRebatePO po) {
        Long orgId = getOrgId();
        Long adminId = getRequestUserId();

        adminLoginUserService.verifyAdvance(po.getAuthType(), po.getVerifyCode(), adminId, orgId, getAdminPlatform());

        boolean success = stakingService.cancelDividend(orgId, po.getProductId(), po.getProductRebateId());
        if (success) {
            return ResultModel.ok();
        } else {
            return ResultModel.error("staking product cancel dividend failed");
        }
    }

    @RequestMapping(value = "/product/order_list")
    public ResultModel<String> queryBrokerProductOrder(@RequestBody @Valid QueryBrokerProductOrderPO po) {
        Long orgId = getOrgId();

        List<StakingProductOrderDTO> orderDTOS = stakingServiceImpl.queryBrokerProductOrder(orgId, po.getProductId(), po.getUserId(), po.getPhone(), po.getEmail(), po.getOrderId(), po.getStartId(), po.getLimit());
        return ResultModel.ok(orderDTOS);
    }


    @RequestMapping("/product/order/repayment_schedule")
    public ResultModel<String> getProductRepaymentSchedule(@RequestBody @Valid ProductRepaymentSchedulePO po) {
        Long orgId = getOrgId();

        List<StakingProductRepaymentScheduleDTO> dtos = stakingServiceImpl.getProductRepaymentSchedule(orgId, po.getOrderId());

        return ResultModel.ok(dtos);
    }

    //获取活期产品用户派息记录
    @RequestMapping("/product/current/repayment_schedule")
    public ResultModel<String> getCurrentProductRepaymentSchedule(@RequestBody @Valid CurrentProductRepaymentSchedulePO po) {
        Long orgId = getOrgId();

        List<CurrentProductRepaymentScheduleDTO> dtos = stakingServiceImpl.getCurrentProductRepaymentSchedule(orgId, po.getUserId(), po.getProductId(), po.getStartId(), po.getSize());

        return ResultModel.ok(dtos);
    }

    /**
     * 查询活期产品资产持仓列表
     *
     * @return
     */
    @RequestMapping(value = "/product/current/asset/list")
    public ResultModel<String> queryCurrentProductAsset(@RequestBody @Valid QueryCurrentProductAssetPO po) {
        Long orgId = getOrgId();
        String language = LocaleUtil.getLanguage();
        List<CurrentProductAssetDTO> assetDTOS = stakingServiceImpl.queryCurrentProductAsset(orgId, po.getProductId(), po.getUserId(), po.getPhone(), po.getEmail(), po.getStartId(), po.getLimit(), language);
        return ResultModel.ok(assetDTOS);

    }

    /**
     * 查询活期产品派息计划列表
     *
     * @return
     */
    @RequestMapping(value = "/product/current/rebate/list")
    public ResultModel<String> getCurrentProductRebateList(@RequestBody @Valid GetCurrentProductRebateListPO po) {
        Long orgId = getOrgId();
        List<CurrentProductRebateDTO> rebateList = stakingServiceImpl.getCurrentProductRebateList(orgId, po.getProductId(), po.getStatus(), po.getStartRebateDate(), po.getSize());
        return ResultModel.ok(rebateList);
    }

    /**
     * 重新计算利息
     *
     * @param po
     * @return
     */
    @RequestMapping(value = "/product/rebate/calc")
    public ResultModel<String> calcProductRebate(@RequestBody @Valid CalcProductRebatePO po) {

        Long orgId = getOrgId();
        Long adminId = getRequestUserId();

        adminLoginUserService.verifyAdvance(po.getAuthType(), po.getVerifyCode(), adminId, orgId, getAdminPlatform());
        try {
            stakingServiceImpl.calcProductRebate(orgId, po);

        } catch (IllegalArgumentException e) {
            return ResultModel.error(e.getMessage());
        }

        return ResultModel.ok();
    }


    /**
     * 查询可见资金账户
     *
     * @return
     */
    @RequestMapping(value = "/product/fund_account/list")
    public ResultModel<Boolean> queryFundAccountShow() {
        Long orgId = getOrgId();
        List<FundAccountDTO> fundAccountDTOList = stakingServiceImpl.queryFundAccountShow(orgId);
        return ResultModel.ok(fundAccountDTOList);

    }


}
