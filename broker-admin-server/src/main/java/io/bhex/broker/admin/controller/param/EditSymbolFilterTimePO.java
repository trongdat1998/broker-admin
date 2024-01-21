package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @author wangsc
 * @description 编辑币对历史k线的过滤时间
 * @date 2020-07-23 10:09
 */
@Data
public class EditSymbolFilterTimePO {
    private String symbol;
    private Long filterTime;
}
