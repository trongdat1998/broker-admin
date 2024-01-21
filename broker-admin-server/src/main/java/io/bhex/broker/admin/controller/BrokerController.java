package io.bhex.broker.admin.controller;

import io.bhex.base.account.ExchangeReply;
import io.bhex.base.account.GetBrokerExchangeContractReply;
import io.bhex.base.account.GetBrokerExchangeContractRequest;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BrokerInfoDTO;
import io.bhex.broker.admin.controller.dto.BrokerWholeConfigDTO;
import io.bhex.broker.admin.controller.param.BrokerInfoPO;
import io.bhex.broker.admin.grpc.client.BrokerClient;
import io.bhex.broker.admin.grpc.client.BrokerFeeClient;
import io.bhex.broker.admin.grpc.client.impl.EarnestClient;
import io.bhex.broker.admin.grpc.client.impl.OrgClient;
import io.bhex.broker.admin.service.BrokerConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 08/08/2018 8:53 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping(path = {"broker", "/api/v1/broker"})
public class BrokerController extends BaseController {

    @Autowired
    private EarnestClient earnestClient;

/*    @Resource
    private BrokerService brokerService;*/

    @Autowired
    private BrokerClient brokerClient;

    @Autowired
    private BrokerConfigService brokerConfigService;

    @Resource
    private BrokerFeeClient brokerFeeClient;

    @Resource
    private OrgClient orgClient;

    //@Resource
    //private OtcClient otcClient;

    @RequestMapping(value = "/get_earnest_address", method = RequestMethod.POST)
    public ResultModel<String> getEarnestAddress(HttpServletRequest request) {
        AdminUserReply adminUserReply = getRequestUser();
        String address = earnestClient.getEarnestAddress(adminUserReply.getOrgId());
        return ResultModel.ok(address);
    }

    @RequestMapping(value = "/modify/broker/info", method = RequestMethod.POST)
    public ResultModel<String> modifyBrokerInfo(HttpServletRequest request, AdminUserReply adminUser,
                                                @RequestBody @Valid BrokerInfoPO brokerInfoPO) {
        brokerClient.updateBrokerFunctionAndLanguage(adminUser.getOrgId(), brokerInfoPO, adminUser);
        return ResultModel.ok();
    }

    /**
     * 修改broker的实时间隔
     * @return
     */
    @RequestMapping(value = "/modify/broker/realtime_interval", method = RequestMethod.POST)
    public ResultModel<String> modifyRealtimeInterval(@RequestBody @Valid BrokerInfoDTO brokerInfoDTO) {
        if (brokerClient.updateRealtimeInterval(getOrgId(), brokerInfoDTO.getRealtimeInterval())) {
            return ResultModel.ok();
        } else {
            return ResultModel.error("modify realtime interval error!" + brokerInfoDTO.getRealtimeInterval());
        }
    }

    /**
     * 修改排行榜是否过滤基础币
     * @return
     */
    @RequestMapping(value = "/modify/broker/filter_top_base_token", method = RequestMethod.POST)
    public ResultModel<String> modifyFilterTopBaseToken(@RequestBody @Valid BrokerInfoDTO brokerInfoDTO) {
        if (brokerClient.updateFilterTopBaseToken(getOrgId(), brokerInfoDTO.getFilterTopBaseToken())) {
            return ResultModel.ok();
        } else {
            return ResultModel.error("modify filter top base token error!" + brokerInfoDTO.getFilterTopBaseToken());
        }
    }

    @AccessAnnotation(verifyLogin = false)
    @RequestMapping(value = "/query/broker/info", method = RequestMethod.POST)
    public ResultModel<String> queryBrokerInfo(HttpServletRequest request) {
        Long orgId = getOrgId();
        BrokerInfoDTO brokerInfoDTO = brokerClient.queryBrokerInfoById(orgId);
        BrokerWholeConfigDTO configDTO = brokerConfigService.getBrokerWholeConfig(orgId);
        Map<Long, Integer> exchangeMap = new HashMap<>();
//        List<ExchangeReply> exchangeReply = orgClient.findAllExchangeByBrokerId(orgId);
//        if (CollectionUtils.isNotEmpty(exchangeReply)) {
//            exchangeReply.forEach(reply ->  exchangeMap.put(reply.getExchangeId(), reply.getIsTrust() ? 1 : 0));
//        }
//        brokerInfoDTO.setIsTrust(exchangeMap);
        brokerInfoDTO.setLogo(configDTO.getLogo());
        brokerInfoDTO.setFavicon(configDTO.getFavicon());

        ExchangeReply exchangeReply = orgClient.findTrustExchangeByBrokerId(orgId);
        brokerInfoDTO.setHasTrustExchange(exchangeReply != null && exchangeReply.getExchangeId() > 0);
        return ResultModel.ok(brokerInfoDTO);
    }

/*    @RequestMapping(value = "/ext/save", method = RequestMethod.POST)
    public ResultModel saveBrokerInfo(@RequestBody BrokerExtDTO dto) {
        Long brokerId=getOrgId();
        if(Objects.isNull(brokerId)||brokerId.longValue()<1){
            log.error("invalid brokerId {}",brokerId);
            return ResultModel.error("brokerId.not.empty");
        }

        if(StringUtils.isBlank(dto.getPhone())){
            return ResultModel.error("phone.not.empty");
        }
        BrokerDetail broker=brokerClient.getByBrokerId(brokerId);
        if(broker.getId()==0){
            return ResultModel.error("broker.not.empty");
        }
        dto.setBrokerId(brokerId);
        dto.setBrokerName(broker.getBrokerName());
        boolean success = otcClient.saveBrokerExt(dto);
        if(success){
            return ResultModel.ok();
        }else{
            return ResultModel.error("fail");
        }
    }

    @RequestMapping(value = "/ext/get", method = RequestMethod.POST)
    public ResultModel<BrokerExtDTO> getBrokerInfo(@RequestBody IdPO idPo) {
        BrokerExtDTO dto = otcClient.getBrokerExt(idPo.getId());
        return ResultModel.ok(dto);
    }*/
}
