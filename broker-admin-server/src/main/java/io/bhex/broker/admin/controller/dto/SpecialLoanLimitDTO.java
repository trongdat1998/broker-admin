package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-16 09:23
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class SpecialLoanLimitDTO {
    Long id;

    Long orgId;

    Long userId;

    Long accountId;

    String loanLimit;

    Integer isOpen;

    Long created;

    Long updated;
}
