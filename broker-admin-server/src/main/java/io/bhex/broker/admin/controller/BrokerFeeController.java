package io.bhex.broker.admin.controller;


import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.DiscountFeeConfigDTO;
import io.bhex.broker.admin.controller.dto.DiscountFeeUserDTO;
import io.bhex.broker.admin.controller.dto.SymbolFeeConfigDTO;
import io.bhex.broker.admin.controller.dto.SymbolMarketAccountDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.BrokerFeeClient;
import io.bhex.broker.grpc.fee.*;
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
@RequestMapping(path = {"/broker/fee", "/api/v1/broker/fee"})
public class BrokerFeeController extends BrokerBaseController {


    @Autowired
    private BrokerFeeClient brokerFeeClient;

    @RequestMapping(value = "/add/discount/config", method = RequestMethod.POST)
    public ResultModel addDiscountConfig(@RequestBody @Valid DiscountFeeConfigPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }
        po.setOrgId(orgId);

        try {
            AddDiscountFeeConfigResponse response = brokerFeeClient.addDiscountFeeConfig(po);
            if (response != null && response.getBasicRet().getCode() == 0) {
                return ResultModel.ok(response.getUserList());
            } else if (response != null) {
                return ResultModel.error(response.getBasicRet().getMsg());
            }
        } catch (Exception ex) {
            log.info("Add discount config error {}", ex);
            return ResultModel.error("Add discount config fail");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/query/discount/config/list", method = RequestMethod.POST)
    public ResultModel queryDiscountFeeConfigList() {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        try {
            List<DiscountFeeConfigDTO> discountFeeConfigList = brokerFeeClient
                    .queryDiscountFeeConfigList(QueryDiscountFeeConfigRequest.newBuilder().setOrgId(orgId).build());
            return ResultModel.ok(discountFeeConfigList);
        } catch (Exception ex) {
            log.info("Query discount fee config list error {}", ex);
            return ResultModel.error("Query discount fee config list error fail");
        }
    }

    @RequestMapping(value = "/query/discount/config", method = RequestMethod.POST)
    public ResultModel queryOneDiscountFeeConfigList(@RequestBody @Valid QueryDiscountFeeConfigPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        try {
            DiscountFeeConfigDTO discountFeeConfigDTO = brokerFeeClient
                    .queryOneDiscountFeeConfig(QueryOneDiscountFeeConfigRequest.newBuilder().setOrgId(orgId).setDiscountId(po.getDiscountId()).build());
            return ResultModel.ok(discountFeeConfigDTO);
        } catch (Exception ex) {
            log.info("Query one discount fee config list error {}", ex);
            return ResultModel.error("Query one discount fee config list error fail");
        }
    }

    @RequestMapping(value = "/save/user/discount/config", method = RequestMethod.POST)
    public ResultModel saveUserDiscountConfig(@RequestBody @Valid UserDiscountFeeConfigPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        try {
            SaveUserDiscountConfigResponse response = brokerFeeClient
                    .saveUserDiscountConfig(SaveUserDiscountConfigRequest
                            .newBuilder()
                            .setOrgId(orgId)
                            .setDiscountId(po.getDiscountId())
                            .setIsBase(1)
                            .setUserId(po.getUserId())
                            .build());
            if (response != null && response.getBasicRet().getCode() == 0) {
                return ResultModel.ok();
            } else if (response != null) {
                return ResultModel.error(response.getBasicRet().getMsg());
            }
        } catch (Exception ex) {
            log.info("Save user discount config error {}", ex);
            return ResultModel.error("Save user discount config fail");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/cancel/user/discount/config", method = RequestMethod.POST)
    public ResultModel cancelUserDiscountConfig(@RequestBody @Valid UserDiscountFeeConfigPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        try {
            CancelUserDiscountConfigResponse response = brokerFeeClient
                    .cancelUserDiscountConfig(CancelUserDiscountConfigRequest
                            .newBuilder()
                            .setOrgId(orgId)
                            .setUserId(po.getUserId())
                            .setIsBase(1)
                            .build());
            if (response != null && response.getBasicRet().getCode() == 0) {
                return ResultModel.ok();
            } else if (response != null) {
                return ResultModel.error(response.getBasicRet().getMsg());
            }
        } catch (Exception ex) {
            log.info("Cancel user discount config error {}", ex);
            return ResultModel.error("Cancel user discount config fail");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/query/user/discount/config", method = RequestMethod.POST)
    public ResultModel queryUserDiscountConfig(@RequestBody @Valid QueryDiscountFeeConfigPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        try {
            DiscountFeeUserDTO userDiscountConfig = brokerFeeClient
                    .queryUserDiscountConfig(QueryUserDiscountConfigRequest.newBuilder().setOrgId(orgId)
                            .setUserId(po.getDiscountId())
                            .setExchangeId(po.getExchangeId() == null ? 0L : po.getExchangeId())
                            .setSymbolId(po.getSymbolId() == null ? "" : po.getSymbolId().trim())
                            .build());
            return ResultModel.ok(userDiscountConfig);
        } catch (Exception ex) {
            log.info("Query user discount config error {}", ex);
            return ResultModel.error("Query user discount config fail");
        }
    }

    @RequestMapping(value = "/add/symbol/config", method = RequestMethod.POST)
    public ResultModel addSymbolFeeConfig(@RequestBody @Valid SymbolFeeConfigPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        try {
            AddSymbolFeeConfigResponse response = brokerFeeClient
                    .addSymbolFeeConfig(AddSymbolFeeConfigRequest
                            .newBuilder()
                            .setOrgId(orgId)
                            .setExchangeId(po.getExchangeId())
                            .setMakerBuyFee(po.getMakerBuyFee().stripTrailingZeros().toPlainString())
                            .setMakerSellFee(po.getMakerSellFee().stripTrailingZeros().toPlainString())
                            .setTakerBuyFee(po.getTakerBuyFee().stripTrailingZeros().toPlainString())
                            .setTakerSellFee(po.getTakerSellFee().stripTrailingZeros().toPlainString())
                            .setId(po.getId())
                            .setSymbolId(po.getSymbolId())
                            .build());
            if (response != null && response.getBasicRet().getCode() == 0) {
                return ResultModel.ok();
            } else if (response != null) {
                return ResultModel.error(response.getBasicRet().getMsg());
            }
        } catch (Exception ex) {
            log.info("Add symbol config error {}", ex);
            return ResultModel.error("Add symbol config fail");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/query/symbol/fee/config/list", method = RequestMethod.POST)
    public ResultModel querySymbolFeeConfigList(@RequestBody @Valid QuerySymbolListPO querySymbolListPO) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        //如果是空默认取币币
        if (querySymbolListPO.getCategory() == null || querySymbolListPO.getCategory().equals(0)) {
            querySymbolListPO.setCategory(1);
        }
        try {
            List<SymbolFeeConfigDTO> symbolFeeConfigList = brokerFeeClient
                    .querySymbolFeeConfigList(QuerySymbolFeeConfigRequest.newBuilder().setOrgId(orgId).setExchangeId(querySymbolListPO.getExchangeId()).setCategory(querySymbolListPO.getCategory()).build());
            return ResultModel.ok(symbolFeeConfigList);
        } catch (Exception ex) {
            log.info("Query symbol fee config list error {}", ex);
            return ResultModel.error("Query symbol fee config list fail");
        }
    }

    @RequestMapping(value = "/save/symbol/market/account", method = RequestMethod.POST)
    public ResultModel saveSymbolMarketAccount(@RequestBody @Valid SymbolMarketAccountPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        try {
            SaveSymbolMarketAccountResponse response = brokerFeeClient
                    .saveSymbolMarketAccount(orgId, po.getSymbolMarketAccountList());
            if (response != null && response.getBasicRet().getCode() == 0) {
                return ResultModel.ok();
            } else if (response != null) {
                return ResultModel.error(response.getBasicRet().getMsg());
            }
        } catch (Exception ex) {
            log.info("Save symbol market account error {}", ex);
            return ResultModel.error("Save symbol market account fail");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/query/symbol/market/account/list", method = RequestMethod.POST)
    public ResultModel querySymbolMarketAccountList(@RequestBody @Valid SymbolMarketAccountDetailPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
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
}

