package io.bhex.broker.admin.service.impl;

import io.bhex.base.account.AccountType;
import io.bhex.bhop.common.grpc.client.BhAccountClient;
import io.bhex.broker.admin.service.OrgAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 11/11/2018 3:38 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class OrgAccountServiceImpl implements OrgAccountService {

    @Autowired
    private BhAccountClient bhAccountClient;

    @Override
    public Long getOrgAccountIdByType(Long orgId, AccountType accountType) {
        Long accountId = bhAccountClient.bindRelation(orgId, accountType);
        return accountId;
    }
}
