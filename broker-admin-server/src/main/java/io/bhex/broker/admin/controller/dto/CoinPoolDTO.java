package io.bhex.broker.admin.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CoinPoolDTO {

    private String tokenId;
    //币池总额
    private String total;
    //已借
    private String borrowed;
    //借出比例
    private String rate;

}
