package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.IntInValid;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-08-18
 */
@Data
public class ConvertSymbolStatusUpdatePO {

    @NotNull
    private Long convertSymbolId;
    @IntInValid({1, 2}) //1启用，2禁用
    private Integer status;

}
