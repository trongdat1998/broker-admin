package io.bhex.broker.admin.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRiskStatisticsDTO {

    private Integer warnNum;
    private Integer appendNum;
    private Integer closeNum;
}
