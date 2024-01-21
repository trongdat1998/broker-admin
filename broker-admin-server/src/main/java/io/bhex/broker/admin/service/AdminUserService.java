package io.bhex.broker.admin.service;

import io.bhex.broker.admin.model.AdminUser;

import java.util.List;

public interface AdminUserService {
    /**
     * get admin user list(status=1 deleted=0, max=100)
     *
     * @param orgId
     * @return
     */
    List<AdminUser> listAdminUser(Long orgId);
}
