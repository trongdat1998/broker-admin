package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * get flow records
 *
 * @author songxd
 * @date 2021-01-16
 */
@Data
public class FlowGetRecordsDTO {
    private Integer bizType;
    private Integer startId;
    private Integer limit;
}
