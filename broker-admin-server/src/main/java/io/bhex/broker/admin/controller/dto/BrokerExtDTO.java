package io.bhex.broker.admin.controller.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Builder
@Data
public class BrokerExtDTO {

    private String phone;

    private String brokerName;

    private Long brokerId;
}

