package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @Description:
 * @Date: 2018/9/30 下午5:34
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Data
public class EditBrokerPO {
    private Long brokerId;
    private boolean enabled;
    private String brokerName;
    private String apiDomain;
    private String privateKey;
    private String publicKey;
}
