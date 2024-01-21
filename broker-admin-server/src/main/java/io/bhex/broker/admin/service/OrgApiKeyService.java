package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.OrgApiKeyDTO;

import java.util.List;

public interface OrgApiKeyService {

    OrgApiKeyDTO createApiKey(Long orgId, String tag, Integer type);

    void updateWhiteIps(Long orgId, Long id, String ips);

    void updateStatus(Long orgId, Long id, Integer status);

    void delete(Long orgId, Long id);

    List<OrgApiKeyDTO> queryApiKeys(Long orgId);

}
