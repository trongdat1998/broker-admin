package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-01-13 17:37
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarginRiskBlackListDTO {
    public Long id;
    public Long orgId;
    public Long userId;
    public Long accountId;
    //配置描述 margin.risk.calculation 表示 杠杆计算黑名单
    public String confGroup;
    public String adminUserName;
    public String reason;
    public Long created;
    public Long updated;

}
