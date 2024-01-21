package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerTokenTaskConfigDTO {

    private Long id;

    private Long orgId;

    @TokenValid
    private String tokenId;

    /**
     * 上架=true 下架=false
     */
    private Boolean published = false;

    /**
     * 允许充币 = true
     */
    private Boolean depositStatus = false;

    /**
     * 允许提币 = true
     */
    private Boolean withdrawStatus = false;

    @NotNull
    private Long actionTime;

    private Integer status = 1;

    private Integer dailyTask;

}
