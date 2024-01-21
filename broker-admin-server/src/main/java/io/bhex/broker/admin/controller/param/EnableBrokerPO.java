package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @Description:
 * @Date: 2018/9/30 下午4:00
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Data
public class EnableBrokerPO {
    private Long brokerId;
    private boolean enabled;
}
