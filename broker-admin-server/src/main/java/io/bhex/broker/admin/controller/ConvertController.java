package io.bhex.broker.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import io.bhex.bhop.common.service.AdminLoginUserService;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.ConvertOrderDTO;
import io.bhex.broker.admin.controller.dto.ConvertSymbolDTO;
import io.bhex.broker.admin.controller.dto.FundAccountDTO;
import io.bhex.broker.admin.controller.param.ConvertOrderQueryPO;
import io.bhex.broker.admin.controller.param.ConvertSymbolCreatePO;
import io.bhex.broker.admin.controller.param.ConvertSymbolModifyPO;
import io.bhex.broker.admin.controller.param.ConvertSymbolStatusUpdatePO;
import io.bhex.broker.admin.service.ConvertService;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.grpc.convert.AddConvertSymbolResponse;
import io.bhex.broker.grpc.convert.ModifyConvertSymbolResponse;
import io.bhex.broker.grpc.convert.UpdateConvertSymbolStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-08-18
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/convert")
public class ConvertController extends BrokerBaseController {

    @Autowired
    private ConvertService convertService;

    @Autowired
    private AdminLoginUserService  adminLoginUserService;

    /**
     * 新增币种
     *
     * @param po
     * @return
     */

    @RequestMapping(value = "/symbol/add", method = RequestMethod.POST)
    public ResultModel addSymbol(@RequestBody @Valid ConvertSymbolCreatePO po) {
        log.info("addSymbol ConvertSymbolCreatePO:{}", new Gson().toJson(po));

        Long brokerId = getOrgId();
        Long adminId = getRequestUserId();
        // 二次校验
        adminLoginUserService.verifyAdvance(po.getAuthType(), po.getVerifyCode(), adminId, brokerId, getAdminPlatform());

        // 校验价格字段数值
        BigDecimal priceValue = new BigDecimal(po.getPriceValue());
        if (priceValue.compareTo(BigDecimal.ZERO) <= 0) {
            return ResultModel.error("convert.price.value.error");
        }
        if (po.getPriceType().equals(2)) {
            // 如果是浮动价格，则价格比例值不超过10000倍
            if (priceValue.compareTo(new BigDecimal(1000000)) >= 0) {
                return ResultModel.error("convert.price.value.error");
            }
        }
        // 校验最大最小数量
        BigDecimal minQty = new BigDecimal(po.getMinQuantity());
        BigDecimal maxQty = new BigDecimal(po.getMaxQuantity());
        if (minQty.compareTo(BigDecimal.ZERO) < 0 || maxQty.compareTo(BigDecimal.ZERO) < 0 ||
                maxQty.compareTo(minQty) < 0) {
            return ResultModel.error("convert.min.max.error");
        }
        // 校验账户每日额度值
        if (new BigDecimal(po.getAccountDailyLimit()).compareTo(BigDecimal.ZERO) < 0) {
            return ResultModel.error("convert.account.daily.limit.error");
        }
        // 校验账户总额度值
        if (new BigDecimal(po.getAccountTotalLimit()).compareTo(BigDecimal.ZERO) < 0) {
            return ResultModel.error("convert.account.total.limit.error");
        }
        // 校验币对每日额度值
        if (new BigDecimal(po.getSymbolDailyLimit()).compareTo(BigDecimal.ZERO) < 0) {
            return ResultModel.error("convert.symbol.daily.limit.error");
        }
        AddConvertSymbolResponse response = convertService.addConvertSymbol(po, getOrgId());
        if (response.getRet() == 0) {
            return ResultModel.ok();
        } else if (response.getRet() == BrokerErrorCode.CONVERT_SYMBOL_IS_EXIST.code()) {
            return ResultModel.error("convert.symbol.is.exist");
        }
        return ResultModel.error("Add convert symbol failed.");
    }

    /**
     * 查询币对信息
     *
     * @return
     */
    @RequestMapping(value = {"/symbol/query"}, method = RequestMethod.GET)
    public ResultModel getSymbols() {
        List<ConvertSymbolDTO> symbolDTOList = convertService.queryConvertSymbol(getOrgId());
        return ResultModel.ok(symbolDTOList);
    }

    /**
     * 修改币对信息
     *
     * @return
     */
    @RequestMapping(value = "/symbol/modify", method = RequestMethod.POST)
    public ResultModel modifyConvertSymbol(@RequestBody @Valid ConvertSymbolModifyPO po) {

        Long brokerId = getOrgId();
        Long adminId = getRequestUserId();
        // 二次校验
        adminLoginUserService.verifyAdvance(po.getAuthType(), po.getVerifyCode(), adminId, brokerId, getAdminPlatform());

        // 校验价格字段数值
        BigDecimal priceValue = new BigDecimal(po.getPriceValue());
        if (priceValue.compareTo(BigDecimal.ZERO) <= 0) {
            return ResultModel.error("convert.price.value.error");
        }
        if (po.getPriceType().equals(2)) {
            // 如果是浮动价格，则价格比例值不超过10000倍
            if (priceValue.compareTo(new BigDecimal(1000000)) >= 0) {
                return ResultModel.error("convert.price.value.error");
            }
        }
        // 校验最大最小数量
        BigDecimal minQty = new BigDecimal(po.getMinQuantity());
        BigDecimal maxQty = new BigDecimal(po.getMaxQuantity());
        if (minQty.compareTo(BigDecimal.ZERO) < 0 || maxQty.compareTo(BigDecimal.ZERO) < 0 ||
                maxQty.compareTo(minQty) < 0) {
            return ResultModel.error("convert.min.max.error");
        }
        // 校验账户每日额度值
        if (new BigDecimal(po.getAccountDailyLimit()).compareTo(BigDecimal.ZERO) < 0) {
            return ResultModel.error("convert.account.daily.limit.error");
        }
        // 校验账户总额度值
        if (new BigDecimal(po.getAccountTotalLimit()).compareTo(BigDecimal.ZERO) < 0) {
            return ResultModel.error("convert.account.total.limit.error");
        }
        // 校验币对每日额度值
        if (new BigDecimal(po.getSymbolDailyLimit()).compareTo(BigDecimal.ZERO) < 0) {
            return ResultModel.error("convert.symbol.daily.limit.error");
        }
        ModifyConvertSymbolResponse response = convertService.modifyConvertSymbol(po, getOrgId());
        if (response.getRet() == 0) {
            return ResultModel.ok();
        }
        return ResultModel.error("Modify convert symbol failed.");
    }

    /**
     * 更新币对状态
     *
     * @return
     */
    @RequestMapping(value = "/symbol/status/update", method = RequestMethod.POST)
    public ResultModel updateConvertSymbolStatus(@RequestBody @Valid ConvertSymbolStatusUpdatePO po) {
        UpdateConvertSymbolStatusResponse response = convertService.updateConvertSymbolStatus(po, getOrgId());
        if (response.getRet() == 0) {
            return ResultModel.ok();
        }
        return ResultModel.error("Update convert symbol failed.");
    }

    /**
     * 查询订单信息
     *
     * @return
     */
    @RequestMapping(value = "/order/query", method = RequestMethod.POST)
    public ResultModel queryConvertOrder(@RequestBody @Valid ConvertOrderQueryPO po) {
        log.info("queryConvertOrder param: {}", JSON.toJSONString(po));
        List<ConvertOrderDTO> orderDTOList = convertService.queryConvertOrders(po, getOrgId());
        return ResultModel.ok(orderDTOList);
    }

    /**
     * 查询可见资金账户
     *
     * @return
     */
    @RequestMapping(value = "/product/fund_account/list")
    public ResultModel<Boolean> queryFundAccountShow() {
        Long orgId = getOrgId();
        List<FundAccountDTO> fundAccountDTOList = convertService.queryFundAccountShow(orgId);
        return ResultModel.ok(fundAccountDTOList);

    }

}
