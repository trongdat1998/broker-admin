package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-12-30 20:18
 */
@Data
public class ChangeMarginPositionStatusPO {
    public Long userId;
    public Integer changeToStatus;
    public Integer curStatus;
}
