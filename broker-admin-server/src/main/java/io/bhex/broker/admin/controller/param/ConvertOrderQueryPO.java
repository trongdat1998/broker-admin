package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-08-18
 */
@Data
public class ConvertOrderQueryPO {
    private Long userId = 0L;
    private Long convertSymbolId = 0L;
    private Integer status = 0;
    private Long beginTime = 0L;
    private Long endTime = 0L;
    private Long lastId = 0L;
    private Integer pageSize = 0;

}
