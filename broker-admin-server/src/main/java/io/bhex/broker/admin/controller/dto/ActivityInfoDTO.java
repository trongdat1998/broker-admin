package io.bhex.broker.admin.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityInfoDTO {
    private Long orgId;

    private Long projectId;

    private String amount;

    private String useAmount;

    private String luckyAmount;

    private String backAmount;
}
