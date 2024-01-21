package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 30/11/2018 3:52 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class AutoAirdropPO {

    private Long id;
    private Long brokerId;
    private String tokenId;
    private Integer airdropType;
    private Integer accountType;
    private Boolean status;
    @Positive
    private BigDecimal airdropTokenNum;
    private Long updatedAt;
    private Long createdAt;
}
