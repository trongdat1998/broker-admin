package io.bhex.broker.admin.controller.internal;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import io.bhex.base.token.TokenCategory;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.ActivityInfoDTO;
import io.bhex.broker.admin.controller.dto.BrokerKycConfigDTO;
import io.bhex.broker.admin.controller.dto.IEOUploadDTO;
import io.bhex.broker.admin.controller.dto.OptionInfoDto;
import io.bhex.broker.admin.controller.dto.SymbolDTO;
import io.bhex.broker.admin.controller.dto.SymbolMarketAccountDTO;
import io.bhex.broker.admin.controller.param.ActivityInfoPO;
import io.bhex.broker.admin.controller.param.EditBrokerPO;
import io.bhex.broker.admin.controller.param.EnableBrokerPO;
import io.bhex.broker.admin.controller.param.IdPO;
import io.bhex.broker.admin.controller.param.OptionCreatePO;
import io.bhex.broker.admin.controller.param.SimpleOptionOrderPO;
import io.bhex.broker.admin.controller.param.SymbolMarketAccountDetailPO;
import io.bhex.broker.admin.controller.param.SymbolMarketAccountPO;
import io.bhex.broker.admin.grpc.client.AgentClient;
import io.bhex.broker.admin.grpc.client.BrokerFeeClient;
import io.bhex.broker.admin.service.ActivityService;
import io.bhex.broker.admin.service.BrokerBasicService;
import io.bhex.broker.admin.service.BrokerService;
import io.bhex.broker.admin.service.OptionService;
import io.bhex.broker.admin.service.UserVerifyService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.admin.AddBrokerKycConfigReply;
import io.bhex.broker.grpc.admin.AddBrokerKycConfigRequest;
import io.bhex.broker.grpc.admin.BrokerKycConfig;
import io.bhex.broker.grpc.admin.QueryOptionListRequest;
import io.bhex.broker.grpc.fee.DeleteAccountTradeFeeConfigRequest;
import io.bhex.broker.grpc.fee.DeleteAccountTradeFeeConfigResponse;
import io.bhex.broker.grpc.fee.QueryAccountTradeFeeConfigRequest;
import io.bhex.broker.grpc.fee.SaveSymbolMarketAccountResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @Description: 为saas提供broker修改的功能
 * @Date: 2018/9/30 下午3:44
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/broker")
public class InternalBrokerController {

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private UserVerifyService userVerifyService;

    @Resource
    private AgentClient agentClient;

    @Resource
    private BrokerBasicService brokerBasicService;

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/enable", method = RequestMethod.POST)
    public ResultModel enableBroker(@RequestBody EnableBrokerPO po) {
        Boolean isOk = brokerService.enableBroker(po.getBrokerId(), po.isEnabled());
        return ResultModel.ok(isOk);
    }

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultModel editBroker(@RequestBody EditBrokerPO po) {
        Boolean isOk = brokerService.updateBroker(po.getBrokerId(), po.getBrokerName(), po.getApiDomain(),
                po.getPrivateKey(), po.getPublicKey(), po.isEnabled());
        return ResultModel.ok(isOk);
    }

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/add_broker_kyc_config", method = RequestMethod.POST)
    public ResultModel addBrokerKycConfig(@RequestBody @Valid BrokerKycConfigDTO po) {
        AddBrokerKycConfigRequest.Builder builder = AddBrokerKycConfigRequest.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(po, builder);
        AddBrokerKycConfigReply reply = userVerifyService.addBrokerKycConfig(builder.build());
        return ResultModel.ok();
    }

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/broker_kyc_config_list", method = RequestMethod.POST)
    public ResultModel getBrokerKycConfigs(@RequestBody @Valid IdPO po) {
        List<BrokerKycConfig> configs = userVerifyService.getBrokerKycConfigs(po.getId());
        List<BrokerKycConfigDTO> result = configs.stream().map(c -> {
            BrokerKycConfigDTO dto = new BrokerKycConfigDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(c, dto);
            return dto;
        }).collect(Collectors.toList());
        return ResultModel.ok(result);
    }

    /**
     * curl http://localhost:7504/api/v1/internal/broker/contract/symbol -d "{'id':}" -H "Accept:
     * application/json" -H "Content-type: application/json"
     */
    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/contract/symbol")
    public ResultModel listContractSymbole(@RequestBody IdPO po) {

        if (po.getId() == null || po.getId().equals(0)) {
            return ResultModel.error("Id not null");
        }

        try {
            Stopwatch sw = Stopwatch.createStarted();

            List<SymbolDTO> list = brokerBasicService.listSymbol(po.getId(), TokenCategory.FUTURE_CATEGORY);

            log.info("listContractSymbole consume {} mills", sw.stop().elapsed(TimeUnit.MILLISECONDS));
            log.info("List contract symbols,[{}]", JSON.toJSONString(list));
            return ResultModel.ok();

        } catch (Exception ex) {
            log.info("List contract symbols, error {}", ex);
            return ResultModel.error("List contract symbols fail");
        }

    }

    @Autowired
    private BrokerFeeClient brokerFeeClient;

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/save/symbol/market/account", method = RequestMethod.POST)
    public ResultModel saveSymbolMarketAccount(@RequestBody @Valid SymbolMarketAccountPO po) {
        if (po.getOrgId() <= 0) {
            return ResultModel.error("Org id not null");
        }
        try {
            SaveSymbolMarketAccountResponse response = brokerFeeClient
                    .saveSymbolMarketAccount(po.getOrgId(), po.getSymbolMarketAccountList());
            if (response != null && response.getBasicRet().getCode() == 0) {
                return ResultModel.ok();
            } else if (response != null) {
                return ResultModel.error(String.valueOf(response.getBasicRet().getCode()));
            }
        } catch (Exception ex) {
            log.info("Save symbol market account error {}", ex);
            return ResultModel.error("Save symbol market account fail");
        }
        return ResultModel.ok();
    }

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/query/symbol/market/account/list", method = RequestMethod.POST)
    public ResultModel querySymbolMarketAccountList(@RequestBody @Valid SymbolMarketAccountDetailPO po) {
        if (po.getOrgId() <= 0) {
            return ResultModel.error("Org id not null");
        }
        try {
            List<SymbolMarketAccountDTO> symbolMarketAccountDTOS = brokerFeeClient
                    .queryAllSymbolMarketAccount(QueryAccountTradeFeeConfigRequest
                            .newBuilder()
                            .setOrgId(po.getOrgId())
                            .setFromId(po.getFromId())
                            .setLimit(po.getLimit())
                            .build());
            return ResultModel.ok(symbolMarketAccountDTOS);
        } catch (Exception ex) {
            log.info("Query symbol market account list error {}", ex);
            return ResultModel.error("Query symbol market account list fail");
        }
    }

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/delete/symbol/market/account", method = RequestMethod.POST)
    public ResultModel deleteSymbolMarketAccount(@RequestBody @Valid SymbolMarketAccountDetailPO po) {

        try {
            DeleteAccountTradeFeeConfigResponse response = brokerFeeClient
                    .deleteSymbolMarketAccount(DeleteAccountTradeFeeConfigRequest
                            .newBuilder()
                            .setId(po.getId())
                            .setOrgId(po.getOrgId())
                            .build());
            if (response.getBasicRet().getCode() == 0) {
                return ResultModel.ok();
            } else {
                return ResultModel.error(response.getBasicRet().getMsg());
            }
        } catch (Exception ex) {
            log.info("Delete symbol market account error {}", ex);
            return ResultModel.error("Delete symbol market account fail");
        }
    }

    @Resource
    private OptionService optionService;

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/option/create", method = RequestMethod.POST)
    public ResultModel<Integer> createNewOption(@RequestBody @Valid OptionCreatePO createPO) {
//        CreateOptionRequest request = CreateOptionRequest.getDefaultInstance();
//        BeanUtils.copyProperties(createPO, request);
//        request.toBuilder().setBrokerId(6001L).build();
        try {
            optionService.createOption(6001L, createPO);
            return ResultModel.ok();
        } catch (Exception ex) {
            log.info("createNewOption error {}", ex);
            return ResultModel.ok();
        }
    }

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/option/current_options", method = RequestMethod.POST)
    public ResultModel<List<OptionInfoDto>> queryCurrentOrders(@RequestBody @Valid SimpleOptionOrderPO po) {
        List<OptionInfoDto> list = new ArrayList<>();
        try {
            list = optionService
                    .queryOptionList(QueryOptionListRequest
                            .newBuilder()
                            .setOrgId(6001L)
                            .setFromId(po.getFromId() != null && po.getFromId() > 0 ? po.getFromId() : 0)
                            .setLimit(po.getPageSize() != null && po.getPageSize() > 0 ? po.getPageSize() : 20)
                            .build());
        } catch (Exception ex) {
            log.info("queryCurrentOrders error {} ", ex);
        }
        if (org.springframework.util.CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(list);
    }

    @Resource
    private ActivityService activityService;

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/query/activity/info")
    public String findProject(@RequestBody ActivityInfoPO activityInfoPO) {
        ActivityInfoDTO activityInfoDTO = activityService.queryActivityProjectInfo(6001, activityInfoPO.getProjectId());
        return new Gson().toJson(activityInfoDTO);
    }

    @AccessAnnotation(internal = true)
    @RequestMapping(value = "/modify/activity/order/Info")
    public String modifyActivityOrderInfo() {
        List<IEOUploadDTO> dtoList = new ArrayList<>();
        IEOUploadDTO l1 = new IEOUploadDTO();
        l1.setId(2901L);
        l1.setUserId(231215678785585152L);
        l1.setOrderId(712334284232686336L);
        l1.setBackAmount("8");
        l1.setLuckyAmount("2");
        l1.setUseAmount("10");
        l1.setAmount("10");

        IEOUploadDTO l2 = new IEOUploadDTO();
        l2.setId(2902L);
        l2.setUserId(231215678785585152L);
        l2.setOrderId(712334314280680192L);
        l2.setBackAmount("7");
        l2.setLuckyAmount("3");
        l2.setUseAmount("10");
        l2.setAmount("10");

        IEOUploadDTO l3 = new IEOUploadDTO();
        l3.setId(2903L);
        l3.setUserId(231215678785585152L);
        l3.setOrderId(712334352901831424L);
        l3.setBackAmount("6");
        l3.setLuckyAmount("4");
        l3.setUseAmount("10");
        l3.setAmount("10");
        dtoList.add(l1);
        dtoList.add(l2);
        dtoList.add(l3);
        this.activityService.modifyActivityOrderInfo(6001L, 1183L, "url", dtoList);
        return null;
    }
}
