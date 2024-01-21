package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-01-06 17:44
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class AccountLoanLimitDTO {
    public Long id;
    public Long orgId;
    public Long userId;
    public Long accountId;
    //1:vip1  2:vip2  3:vip3
    public Integer vipLevel;
    public Long created;
    public Long updated;
}
