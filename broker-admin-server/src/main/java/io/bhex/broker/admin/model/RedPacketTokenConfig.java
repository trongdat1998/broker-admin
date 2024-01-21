package io.bhex.broker.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketTokenConfig {

    private Long id;
    private Long orgId;
    private String tokenId;
    private String tokenName;
    private String minAmount; // sing redPacket minPrecision and minAmount
    private String maxAmount; // sing redPacket maxAmount
    private Integer maxCount;
    private String maxTotalAmount;
    private Integer status;
    private Integer customOrder;
//    private Long created;
//    private Long updated;

}
