package io.bhex.broker.admin.controller.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AgentCommissionDTO {

    private String id;

    private String time;

    private Long brokerId;

    private String userId;

    private String agentName;

    private String tokenId;

    private String superiorName;

    private String superiorUserId;

    private String agentFee;
}
