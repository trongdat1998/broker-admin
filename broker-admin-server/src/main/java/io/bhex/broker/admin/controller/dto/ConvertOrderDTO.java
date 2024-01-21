package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvertOrderDTO {

    private Long id;
    private Long orderId;
    private Long userId;
    private Long accountId;
    private Long brokerAccountId;
    private Long convertSymbolId;
    private String purchaseTokenId;
    private String purchaseTokenName;
    private String offeringsTokenId;
    private String offeringsTokenName;
    private String purchaseQuantity;
    private String offeringsQuantity;
    private String price;
    private Integer status;
    private String errorMessage;
    private Long created;
    private Long updated;

}
