package io.bhex.broker.admin.service;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.broker.admin.controller.dto.BrokerWholeConfigDTO;
import io.bhex.broker.admin.controller.dto.IndexCustomerConfigDTO;
import io.bhex.broker.admin.controller.param.SaveBrokerWholeConfigPO;
import io.bhex.broker.grpc.admin.*;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 3:53 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BrokerConfigService {

     enum SaveOrPreview {
        SAVE(SaveType.SAVE), PREVIEW(SaveType.PREVIEW);

        private SaveType saveType;

        SaveOrPreview(SaveType saveType) {
            this.saveType = saveType;
        }

        public SaveType getSaveType() {
            return saveType;
        }
    }

    BrokerWholeConfigDTO getBrokerWholeConfig(Long brokerId);

    Boolean saveBrokerWholeConfig(SaveBrokerWholeConfigPO param, SaveOrPreview saveOrPreview);

    SaveConfigReply editIndexCustomerConfig(long brokerId, IndexCustomerConfigDTO request, int configType);

    IndexCustomerConfigDTO getIndexCustomerConfig(long brokerId, int status, int configType);

    //切换到新版本首页模式
    Boolean switchIndexToNewVersion(AdminUserReply userReply, boolean init);
}
