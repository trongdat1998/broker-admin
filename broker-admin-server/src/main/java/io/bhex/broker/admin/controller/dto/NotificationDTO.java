package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class NotificationDTO {

    //1=kyc-application,2=otc-appeal,3=withdraw
    private Integer authId;

    private Integer number;
}
