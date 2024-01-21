package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtcThirdPartyOrderDTO {

    private Long orderId;
    private String clientOrderId;
    private Long otcSymbolId;
    private String tokenId;
    private String currencyId;
    private String tokenAmount;
    private String currencyAmount;
    private String price;
    private String feeAmount;
    private Integer side;
    private Long orgId;
    private Long userId;
    private Long accountId;
    private Integer status;
    private String errorMessage;
    private String thirdPartyName;
    private Long created;
    private Long updated;
}
