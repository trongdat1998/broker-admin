package io.bhex.broker.admin.controller.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Builder
@Data
public class OTCOrderContactDTO {
    private BrokerExtDTO brokerInfo;

    private String uid;

    private String mobile;

    private String email;

    private Boolean showDetail;

    private Boolean showSendSMS;

}
