package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class DepositAddressDTO {

    private String tokenId;
    private String chainType;
    private String address;
    private String addressExt;
    private String qrcode;
    private Integer requiredConfirmNum;
    private Boolean hasMultiChainType;
    @Singular("multiAddresses")
    private List<DepositAddressDTO> multiDepositAddress;

}
