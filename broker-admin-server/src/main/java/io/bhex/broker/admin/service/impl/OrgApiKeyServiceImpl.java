package io.bhex.broker.admin.service.impl;

import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.controller.dto.OrgApiKeyDTO;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.admin.service.OrgApiKeyService;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.org_api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service(value = "orgApiKeyService")
public class OrgApiKeyServiceImpl implements OrgApiKeyService {

    @Resource
    GrpcClientConfig grpcConfig;

    private OrgApiKeyServiceGrpc.OrgApiKeyServiceBlockingStub getStub() {
        return grpcConfig.orgApiKeyServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public OrgApiKeyDTO createApiKey(Long orgId, String tag, Integer type) {
        CreateOrgApiKeyRequest request = CreateOrgApiKeyRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTag(tag)
                .setType(type)
                .build();
        CreateOrgApiKeyResponse response = getStub().createOrgApiKey(request);
        return getApiKeyDTO(response.getApiKey());
    }

    @Override
    public void updateWhiteIps(Long orgId, Long id, String ips) {
        UpdateOrgApiKeyIpsRequest request = UpdateOrgApiKeyIpsRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setApiKeyId(id)
                .setIpWhiteList(ips)
                .build();
        UpdateOrgApiKeyResponse response = getStub().updateOrgApiKeyIps(request);
        if (response.getBasicRet().getCode() != 0) {
            throw new BizException(ErrorCode.ERROR);
        }
    }

    @Override
    public void updateStatus(Long orgId, Long id, Integer status) {
        UpdateOrgApiKeyStatusRequest request = UpdateOrgApiKeyStatusRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setApiKeyId(id)
                .setStatus(status)
                .build();
        UpdateOrgApiKeyResponse response = getStub().updateOrgApiKeyStatus(request);
        if (response.getBasicRet().getCode() != 0) {
            throw new BizException(ErrorCode.ERROR);
        }
    }

    @Override
    public void delete(Long orgId, Long id) {
        DeleteOrgApiKeyRequest request = DeleteOrgApiKeyRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setApiKeyId(id)
                .build();
        DeleteOrgApiKeyResponse response = getStub().deleteOrgApiKey(request);
        if (response.getBasicRet().getCode() != 0) {
            throw new BizException(ErrorCode.ERROR);
        }
    }

    @Override
    public List<OrgApiKeyDTO> queryApiKeys(Long orgId) {
        QueryOrgApiKeyRequest request = QueryOrgApiKeyRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        QueryOrgApiKeyResponse response = getStub().queryOrgApiKeys(request);
        return response.getApiKeysList().stream()
                .map(this::getApiKeyDTO)
                .collect(Collectors.toList());
    }

    private OrgApiKeyDTO getApiKeyDTO(OrgApiKey apiKey) {
        return OrgApiKeyDTO.builder()
                .id(apiKey.getId())
                .apiKey(apiKey.getApiKey())
                .secretKey(apiKey.getSecretKey())
                .tag(apiKey.getTag())
                .ipWhiteList(apiKey.getIpWhiteList())
                .status(apiKey.getStatus())
                .type(apiKey.getType())
                .level(apiKey.getLevel())
                .created(apiKey.getCreated())
                .updated(apiKey.getUpdated())
                .build();
    }

}
