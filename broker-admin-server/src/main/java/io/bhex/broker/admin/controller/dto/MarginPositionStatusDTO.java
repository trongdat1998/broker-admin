package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-12-30 19:40
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarginPositionStatusDTO {
    public Long userId;
    public Long accountId;
    //0:正常  1：冻结
    public int curStatus;
}
