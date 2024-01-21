package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-08-18
 */
@Data
public class OtcThirdPartyOrderQueryPO {
    private Integer status = 0;
    private Long orderId = 0L;
    private Long userId = 0L;
    private Long startTime = 0L ;
    private Long endTime = 0L;
    private Long lastId = 0L;
    private Integer limit = 20;
}
