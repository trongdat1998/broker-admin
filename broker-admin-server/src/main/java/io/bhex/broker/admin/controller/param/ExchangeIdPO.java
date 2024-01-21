package io.bhex.broker.admin.controller.param;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2018/9/30 下午5:34
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Data
public class ExchangeIdPO {
    @NotNull
    private Long exchangeId;
}
