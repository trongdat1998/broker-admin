package io.bhex.broker.admin.service.impl;

import io.bhex.base.admin.common.AccountType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.dto.param.CreateAdminUserPO;
import io.bhex.bhop.common.service.AdminUserExtensionService;
import io.bhex.broker.admin.service.BrokerConfigService;
import io.bhex.broker.admin.service.BrokerService;
import io.bhex.broker.admin.service.SymbolService;
import io.bhex.broker.admin.service.TokenService;
import io.bhex.broker.admin.util.SignUtil;
import io.bhex.broker.grpc.admin.CreateBrokerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 16/09/2018 3:56 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class AdminUserExtensionServiceImpl implements AdminUserExtensionService {

    @Autowired
    private BrokerService brokerService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private BrokerConfigService brokerConfigService;
    @Autowired
    private SymbolService symbolService;

    @Override
    public void afterCreateUser(CreateAdminUserPO userPO) {
        SignUtil.BrokerKey key = getKeyPair();
        CreateBrokerRequest request = CreateBrokerRequest.newBuilder()
                .setOrgId(userPO.getOrgId())
                .setBrokerName(userPO.getOrgName())
                .setApiDomain(userPO.getBrokerWebDomain())
                .setPrivateKey(key.getPrivateKey())
                .setPublicKey(key.getPublicKey())
                .build();
        brokerService.createBroker(request);

        tokenService.syncBhexTokens(userPO.getOrgId());

        AdminUserReply userReply = AdminUserReply.newBuilder()
                .setOrgId(userPO.getOrgId())
                .setAccountType(AccountType.ROOT_ACCOUNT)
                .setUsername(userPO.getUsername())
                .setEmail(userPO.getEmail())
                .build();
        brokerConfigService.switchIndexToNewVersion(userReply, true);
        symbolService.initCustomerQuoteTokens(userPO.getOrgId(), userReply);
    }

    public SignUtil.BrokerKey getKeyPair() {
        SignUtil.BrokerKey brokerKey = SignUtil.getBrokerKey(SignUtil.getENCRYPT_PASSWORD());
        return brokerKey;
    }
}
