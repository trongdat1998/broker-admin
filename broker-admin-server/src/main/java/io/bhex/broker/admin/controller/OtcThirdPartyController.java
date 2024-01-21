package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.OtcThirdPartyDTO;
import io.bhex.broker.admin.controller.dto.OtcThirdPartyDisclaimerDTO;
import io.bhex.broker.admin.controller.dto.OtcThirdPartyOrderDTO;
import io.bhex.broker.admin.controller.param.OtcThirdPartyDisclaimerUpdatePO;
import io.bhex.broker.admin.controller.param.OtcThirdPartyOrderQueryPO;
import io.bhex.broker.admin.service.OtcThirdPartyService;
import io.bhex.broker.grpc.otc.third.party.UpdateOtcThirdPartyDisclaimerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-08-18
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/otc/third_party")
public class OtcThirdPartyController extends BrokerBaseController {

    @Autowired
    private OtcThirdPartyService otcThirdPartyService;

    /**
     * 查询otc第三方机构
     *
     * @return
     */

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultModel getOtcThirdParty() {
        List<OtcThirdPartyDTO> thirdPartyList = otcThirdPartyService.getOtcThirdParty(getOrgId());
        return ResultModel.ok(thirdPartyList);
    }

    @RequestMapping(value = "/disclaimer/query", method = RequestMethod.GET)
    public ResultModel queryDisclaimer() {
        List<OtcThirdPartyDisclaimerDTO> disclaimerList = otcThirdPartyService.queryOtcThirdPartyDisclaimer(getOrgId());
        return ResultModel.ok(disclaimerList);
    }

    @RequestMapping(value = "/disclaimer/update", method = RequestMethod.POST)
    public ResultModel updateDisclaimer(@RequestBody @Valid OtcThirdPartyDisclaimerUpdatePO po) {
        UpdateOtcThirdPartyDisclaimerResponse response = otcThirdPartyService.updateOtcThirdPartyDisclaimer(po, getOrgId());
        if (response.getRet() == 0) {
            return ResultModel.ok();
        }
        return ResultModel.error("update disclaimer failed!");
    }

    @RequestMapping(value = "/order/query", method = RequestMethod.POST)
    public ResultModel queryOtcThirdPartyOrders(@RequestBody @Valid OtcThirdPartyOrderQueryPO po) {
        List<OtcThirdPartyOrderDTO> orderList = otcThirdPartyService.queryOtcThirdPartyOrders(po, getOrgId());
        return ResultModel.ok(orderList);
    }


}
