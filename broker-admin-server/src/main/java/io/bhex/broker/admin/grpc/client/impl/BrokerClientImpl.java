package io.bhex.broker.admin.grpc.client.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.EditReply;
import io.bhex.bhop.common.config.OrgInstanceConfig;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.dto.param.BrokerInstanceRes;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.constants.GrpcConstant;
import io.bhex.broker.admin.constants.RealtimeIntervalEnum;
import io.bhex.broker.admin.controller.dto.BaseConfigDTO;
import io.bhex.broker.admin.controller.dto.BrokerInfoDTO;
import io.bhex.broker.admin.controller.dto.BrokerLanguageDTO;
import io.bhex.broker.admin.controller.dto.ExchangeContractDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.BrokerInfoPO;
import io.bhex.broker.admin.grpc.client.BrokerClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.service.ExchangeContractService;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.function.config.BrokerFunction;
import io.bhex.broker.grpc.function.config.SetBrokerFunctionConfigRequest;
import io.bhex.broker.grpc.function.config.SetBrokerFunctionConfigResponse;
import io.bhex.ex.otc.InitOtcConfigRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 16/08/2018 5:03 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Component
public class BrokerClientImpl implements BrokerClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Autowired
    private OtcClient otcClient;

    @Autowired
    private ExchangeContractService exchangeContractService;

    @Autowired
    private OrgInstanceConfig orgInstanceConfig;

    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;

    private AdminBrokerServiceGrpc.AdminBrokerServiceBlockingStub getBrokerStub() {
        return grpcConfig.adminBrokerServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public boolean createBroker(CreateBrokerRequest request) {
        return getBrokerStub().createBroker(request).getResult();
    }

    @Override
    public BrokerDetail getByBrokerId(Long brokerId) {
        GetBrokerByBrokerIdRequest request = GetBrokerByBrokerIdRequest.newBuilder()
                .setBrokerId(brokerId)
                .build();

        return getBrokerStub().getBrokerByBrokerId(request);
    }

    @Override
    public boolean enableBroker(Long brokerId, boolean enabled) {
        EnableBrokerRequest request = EnableBrokerRequest.newBuilder().setBrokerId(brokerId).setEnabled(enabled).build();
        EnableBrokerReply reply = getBrokerStub().enableBroker(request);
        log.info("request:{} reply:{}", request, reply);
        return reply.getResult();
    }

    @Override
    public boolean updateBroker(Long brokerId, String brokerName, String apiDomain, String privateKey, String publicKey, boolean enabled) {
        UpdateBrokerRequest.Builder builder = UpdateBrokerRequest.newBuilder()
                .setOrgId(brokerId)
                .setStatus(enabled ? 1 : 0);
        if (!StringUtils.isEmpty(brokerName)) {
            builder.setBrokerName(brokerName);
        }
        if (!StringUtils.isEmpty(apiDomain)) {
            builder.setApiDomain(apiDomain);
        }
//        if (!StringUtils.isEmpty(privateKey)) {
//            builder.setPrivateKey(privateKey);
//        }
//        if (!StringUtils.isEmpty(publicKey)) {
//            builder.setPublicKey(publicKey);
//        }
        UpdateBrokerRequest request = builder.build();

        UpdateBrokerReply reply = getBrokerStub().updateBroker(request);
        log.info("request:{} reply:{}", request, reply);
        return reply.getResult();
    }

    @Override
    public boolean updateBrokerSignName(Long brokerId, String sign) {
        UpdateBrokeSignNameRequest request = UpdateBrokeSignNameRequest.newBuilder()
                .setBrokerId(brokerId)
                .setSignName(sign)
                .build();
        UpdateBrokeSignNameReply reply = getBrokerStub().updateBrokeSignName(request);
        log.info("request:{}, reply:{}", request, reply);
        return reply.getResult();
    }

    @Override
    public boolean updateBrokerFunctionAndLanguage(Long brokerId, BrokerInfoPO brokerInfoPO, AdminUserReply adminUser) {
        UpdateBrokerRequest.Builder builder = UpdateBrokerRequest.newBuilder()
                .setOrgId(brokerId);
        log.info("update broker info {}", JSON.toJSONString(brokerInfoPO));

        if (brokerInfoPO.getFunctions() == null && brokerInfoPO.getSupportLanguages().size() == 0) {
            return Boolean.FALSE;
        }

        if (brokerInfoPO.getFunctions() != null) {
            builder.setFunctions(JSON.toJSONString(brokerInfoPO.getFunctions()));
        }

        if (brokerInfoPO.getSupportLanguages() != null && brokerInfoPO.getSupportLanguages().size() > 0) {
            builder.setSupportLanguages(JSON.toJSONString(brokerInfoPO.getSupportLanguages()));
        }
        UpdateBrokerRequest request = builder.build();
        UpdateBrokerReply reply = getBrokerStub().updateBrokerFunctionAndLanguage(request);
        log.info("request:{} reply:{}", request, reply);

        if (brokerInfoPO.getFunctions() != null && !brokerInfoPO.getFunctions().getOrDefault("userLevel", false)) {
            BaseConfigPO configPO = new BaseConfigPO();
            configPO.setGroup("user.level.config");
            configPO.setKey("open.switch");
            configPO.setValue("false");
            configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
            configPO.setLanguage(null);
            configPO.setWithLanguage(false);
            configPO.setStatus(0);
            EditReply editReply = baseConfigService.editConfig(brokerId, configPO, adminUser);
            if (editReply.getCode() != 0) {
                return Boolean.FALSE;
            }
        }


        if (brokerInfoPO.getFunctions() != null && brokerInfoPO.getFunctions().get("otc")) {
            //otc默认开始白名单
            SetBrokerFunctionConfigRequest otcReq = SetBrokerFunctionConfigRequest.newBuilder()
                    .setHeader(Header.newBuilder().setOrgId(brokerId).build())
                    .setFunction(BrokerFunction.OTC_WHITE_LIST)
                    .setStatus(1)
                    .build();
            SetBrokerFunctionConfigResponse otcResp = otcClient.setWhiteListStatus(otcReq);
            log.warn("enable the otc whitelist,result={}", otcResp.getRet());

            //尝试初始化
            PaginationVO<ExchangeContractDTO> exchangeContractDTOPaginationVO
                    = exchangeContractService.listExchangeContract(brokerId, 1, 100);
            log.info("exchangeContractDTOPaginationVO info {}", JSON.toJSONString(exchangeContractDTOPaginationVO));
            if (exchangeContractDTOPaginationVO.getTotal() > 0) {
                Long exchangeId = exchangeContractDTOPaginationVO.getList().get(0).getExchangeId();
                otcClient.initOtcConfig(InitOtcConfigRequest.newBuilder().setOrgId(brokerId).setExchangeId(exchangeId).build());
            } else {
                log.info("updateBrokerFunctionAndLanguage fail otc exchange id not find orgId {}", brokerId);
                return Boolean.FALSE;
            }
        }
        return reply.getResult();
    }

    @Override
    public boolean updateRealtimeInterval(Long brokerId, String realtimeInterval) {
        if (brokerId == null) {
            return false;
        }
        //判断是否
        RealtimeIntervalEnum realtimeIntervalEnum = RealtimeIntervalEnum.intervalOf(realtimeInterval);
        if (realtimeIntervalEnum == null) {
            return false;
        }
        UpdateBrokerRequest request = UpdateBrokerRequest.newBuilder()
                .setOrgId(brokerId)
                .setRealtimeInterval(realtimeIntervalEnum.getInterval())
                .build();
        log.info("update broker realtime interval! brokerId={},realtimeInterval={}", brokerId, realtimeInterval);
        UpdateBrokerReply reply = getBrokerStub().updateBroker(request);
        return reply.getResult();
    }

    /**
     * 更新broker实时间隔
     */
    @Override
    public boolean updateFilterTopBaseToken(Long brokerId, Boolean filterTopBaseToken) {
        UpdateBrokerFilterTopBaseTokenRequest request = UpdateBrokerFilterTopBaseTokenRequest.newBuilder()
                .setBrokerId(brokerId)
                .setFilterTopBaseToken(Boolean.TRUE.equals(filterTopBaseToken))
                .build();
        UpdateBrokerFilterTopBaseTokenReply reply = getBrokerStub().updateBrokerFilterTopBaseToken(request);
        log.info("request:{}, reply:{}", request, reply);
        return reply.getResult();
    }

    @Override
    public BrokerInfoDTO queryBrokerInfoById(Long brokerId) {
        GetBrokerByBrokerIdRequest request = GetBrokerByBrokerIdRequest.newBuilder()
                .setBrokerId(brokerId)
                .build();

        BrokerDetail brokerDetail
                = getBrokerStub().getBrokerByBrokerId(request);
        Gson gson = new Gson();

        List<BrokerLanguageDTO> languageList;
        if (StringUtils.isNotEmpty(brokerDetail.getSupportLanguages())) {
            languageList = JsonUtil.defaultGson().fromJson(brokerDetail.getSupportLanguages(), new com.google.gson.reflect.TypeToken<List<BrokerLanguageDTO>>() {
            }.getType());
        } else {
            languageList = new ArrayList<>();
        }

        for (BrokerLanguageDTO dto : languageList) {
            String language = dto.getLanguage();
            if (language.contains("-")) {
                language = language.split("-")[0] + "_" + language.split("-")[1].toUpperCase();
            }
            BaseConfigPO configPO = new BaseConfigPO();
            configPO.setGroup(BizConstant.SITE_LANGUAGE_GROUP);
            configPO.setKey("adminBroker");

            configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BH);
            configPO.setLanguage(language);
            configPO.setWithLanguage(true);

            BaseConfigDTO configDTO = baseConfigService.getOneConfig(0L, configPO);
            if (configDTO != null) {
                List<String> jsLoadUrls = new ArrayList<>();
                jsLoadUrls.add(configDTO.getValue());

                configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
                configDTO = baseConfigService.getOneConfig(brokerId, configPO);
                if (configDTO != null) {
                    jsLoadUrls.add(configDTO.getValue());
                }
                dto.setJsLoadUrls(jsLoadUrls);
            }
        }

        BrokerInstanceRes brokerInstanceRes = orgInstanceConfig.getBrokerInstance(brokerId);

        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(BizConstant.CUSTOM_CONFIG_GROUP);
        configPO.setKey(BizConstant.INDEX_NEW_VERSION);
        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(null);
        configPO.setWithLanguage(true);
        configPO.setStatus(1);
        BaseConfigDTO configDTO = baseConfigService.getOneConfig(brokerId, configPO);
        boolean indexNewVersion = configDTO != null && Strings.nullToEmpty(configDTO.getValue()).equals("true");

        return BrokerInfoDTO
                .builder()
                .functions(StringUtils.isEmpty(brokerDetail.getFunctions()) ? new HashMap<>() : gson.fromJson(brokerDetail.getFunctions(), Map.class))
                .supportLanguages(languageList)
                .orgId(brokerId)
                .brokerName(brokerDetail.getBrokerName())
                .apiDomain(brokerDetail.getApiDomain())
                .superior(brokerDetail.getSuperior() == 1)
                .canCreateOrgApi(brokerDetail.getCanCreateOrgApi())
                .frontendCustomer(false)
                .indexNewVersion(indexNewVersion)
                .frontendCustomer(brokerInstanceRes.getFrontendCustomer() == 1)
                .dueTime(brokerInstanceRes.getDueTime())
                .remainTime(brokerInstanceRes.getDueTime() - System.currentTimeMillis())
                .realtimeInterval(brokerDetail.getRealtimeInterval())
                .filterTopBaseToken(brokerDetail.getFilterTopBaseToken())
                .build();
    }


}
