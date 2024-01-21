package io.bhex.broker.admin.service;

import io.bhex.bhop.common.dto.param.PlatformAccountBindAccountPO;
import io.bhex.broker.admin.controller.param.BrokerPlatformAccountBindPO;
import lombok.Data;

/**
 * @Description:
 * @Date: 2018/10/10 下午5:07
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface PlatformAccountBindService {
    @Data
    static class InnerResult{
        private Long accountId;
        private String username;
        private boolean usernameIsEmail;
        private String errorMsg;
        private Long brokerId;
    }

    InnerResult checkBindInput(Long myBrokerId, String brokerName, String username, int accountType);

    InnerResult checkVerifyCode(Long myBrokerId, String inputValidateCode, int accountType, Long accountId);


    boolean sendVerifyCode(Long myBrokerId, String username, int accountType, InnerResult result);
}
