package io.bhex.broker.admin.controller.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryBrokerAgentUserDTO {

    private String id;

    private String userId;

    private String mobile;

    private String email;

    private String userName;

    private String country;

    private String agentName;

    private String agentUserId;

    private Integer agentLevel;

    private String registerTime;

    private Integer status;
}
