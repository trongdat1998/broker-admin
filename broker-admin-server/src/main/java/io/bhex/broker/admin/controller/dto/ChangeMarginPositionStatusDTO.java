package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-12-30 19:43
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeMarginPositionStatusDTO {
    public Integer curStatus;
}
