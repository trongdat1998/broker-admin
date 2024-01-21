package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import io.bhex.broker.admin.controller.dto.CommonConfigDTO;
import io.bhex.broker.admin.controller.param.CommonConfigPO;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.admin.service.CommonConfigService;
import io.bhex.broker.grpc.common_ini.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("commonConfigService")
public class CommonConfigServiceImpl implements CommonConfigService {

    public static final String CUSTOM_KEY_PREFIX = "custom_";

    @Resource
    GrpcClientConfig grpcConfig;

    private CommonIniServiceGrpc.CommonIniServiceBlockingStub getStub() {
        return grpcConfig.commonIniServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public CommonConfigDTO setCommonConfig(Long orgId, CommonConfigPO po) {
        SaveCommonIniRequest request = SaveCommonIniRequest.newBuilder()
                .setOrgId(orgId)
                .setIniName(CUSTOM_KEY_PREFIX + po.getKey())
                .setIniDesc(Strings.nullToEmpty(po.getDesc()))
                .setIniValue(po.getValue())
                .build();
        SaveCommonIniResponse response = getStub().saveCommonIni(request);
        return CommonConfigDTO.builder()
                .key(po.getKey())
                .desc(response.getCommonIni().getIniDesc())
                .value(response.getCommonIni().getIniValue())
                .build();
    }

    @Override
    public CommonConfigDTO getCommonConfig(Long orgId, String key) {
        GetCommonIni2Response response = getStub().getCommonIni2(GetCommonIni2Request.newBuilder()
                .setOrgId(orgId)
                .setIniName(CUSTOM_KEY_PREFIX + key)
                .build());
        return CommonConfigDTO.builder()
                .key(key)
                .desc(response.getInis().getIniDesc())
                .value(response.getInis().getIniValue())
                .build();
    }

}
