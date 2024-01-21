package io.bhex.broker.admin.controller.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OTCShareSymbolDTO {

    private String tokenId;

    private String currencyId;

    //状态，1=未共享,2=共享开启,3=共享关闭
    private Integer status;

}
