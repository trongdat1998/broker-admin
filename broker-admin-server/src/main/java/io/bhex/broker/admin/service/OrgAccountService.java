package io.bhex.broker.admin.service;

import io.bhex.base.account.AccountType;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 11/11/2018 3:36 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface OrgAccountService {

    Long getOrgAccountIdByType(Long orgId, AccountType accountType);
}
