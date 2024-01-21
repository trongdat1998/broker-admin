package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentProductAssetDTO {

    private Long id;

    private Long userId;

    private Long productId;

    private String productName;

    private String amount;

    //产品到期时间
    private Long productEndDate;

    private String tokenId;

    private String tokenName;

    private Integer productStatus;


}
