package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-03-02 09:46
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialInterestDTO {
    private Long id;
    private Long orgId;
    private Long userId;
    private Long accountId;
    private String tokenId;
    private String interest;
    private Integer effectiveFlag;
    private Long created;
    private Long updated;
    private String showInterest;
}
