package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import io.bhex.base.admin.common.AccountType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.grpc.client.AdminUserClient;
import io.bhex.broker.admin.model.AdminUser;
import io.bhex.broker.admin.service.AdminUserService;
import io.bhex.broker.admin.util.AdminUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AdminUserServiceImpl
 */
@Service
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserClient adminUserClient;

    @Override
    public List<AdminUser> listAdminUser(Long orgId) {
        try {
            List<AdminUserReply> listReply = adminUserClient.listAdminUserByOrgId(orgId);
            if (CollectionUtils.isEmpty(listReply)) {
                return new ArrayList<>();
            }
            return listReply.stream().map(this::getAdminUser).collect(Collectors.toList());
        } catch (Exception ex){
            log.error("admin user get list by orgid error:{}-{}", orgId, ex.getMessage());
            return new ArrayList<>();
        }
    }

    private AdminUser getAdminUser(AdminUserReply reply){
        return AdminUser.builder()
                .id(reply.getId())
                .name(Strings.isNullOrEmpty(reply.getRealName()) ? AdminUtils.emailEncrypt(reply.getEmail()) : reply.getRealName())
                .email("")
                .phone("")
                .password("")
                .build();
    }
}
