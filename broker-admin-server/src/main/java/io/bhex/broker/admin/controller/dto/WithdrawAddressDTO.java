package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawAddressDTO {
    //private Long id;

    //private Long userId;

    private String tokenId;

    private String tokenName;

    private String address;

    private String remark;

    //private Long created;

    //private Long updated;
}
