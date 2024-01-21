package io.bhex.broker.admin.controller.param;

import lombok.Data;

@Data
public class QueryTokenTasksPO {
    private Long fromId = 0L;

    private Integer pageSize = 30;

}
